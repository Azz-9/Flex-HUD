param(
    [string[]] $Branches = @(
    "26.1",
    "26.2"
),

    [string] $ReleaseBranch = "26.2",

    [switch] $DryRun
)

$ErrorActionPreference = "Stop"

$repositoryDirectory = $PSScriptRoot
$releaseDirectory = Join-Path $repositoryDirectory "release-jars"

function Get-GradleProperty
{
    param(
        [Parameter(Mandatory = $true)]
        [string] $Name
    )

    $gradlePropertiesPath = Join-Path $repositoryDirectory "gradle.properties"

    $line = Get-Content $gradlePropertiesPath |
            Where-Object {
                $_ -match "^\s*$([regex]::Escape($Name) )\s*="
            } |
            Select-Object -First 1

    if ($null -eq $line)
    {
        throw "Property '$Name' not found in gradle.properties."
    }

    return ($line -split "=", 2)[1].Trim()
}

function Invoke-Gradle
{
    param(
        [Parameter(Mandatory = $true)]
        [string[]] $Arguments
    )

    if ($DryRun)
    {
        Write-Host "DRY RUN: gradlew $( $Arguments -join ' ' )"
        return
    }

    & "$repositoryDirectory\gradlew.bat" @Arguments

    if ($LASTEXITCODE -ne 0)
    {
        throw "Gradle failed: gradlew $( $Arguments -join ' ' )"
    }
}

function Invoke-Git
{
    param(
        [Parameter(Mandatory = $true)]
        [string[]] $Arguments
    )

    if ($DryRun)
    {
        Write-Host "DRY RUN: git $( $Arguments -join ' ' )"
        return
    }

    & git -C $repositoryDirectory @Arguments

    if ($LASTEXITCODE -ne 0)
    {
        throw "Git failed: git $( $Arguments -join ' ' )"
    }
}

function Test-LocalGitTagExists
{
    param(
        [Parameter(Mandatory = $true)]
        [string] $TagName
    )

    if ($DryRun)
    {
        Write-Host "DRY RUN: git tag --list $TagName"
        return $false
    }

    $existingTag = git -C $repositoryDirectory tag --list $TagName

    if ($LASTEXITCODE -ne 0)
    {
        throw "Unable to check whether local tag '$TagName' exists."
    }

    return -not [string]::IsNullOrWhiteSpace($existingTag)
}

function Test-RemoteGitTagExists
{
    param(
        [Parameter(Mandatory = $true)]
        [string] $TagName
    )

    if ($DryRun)
    {
        Write-Host "DRY RUN: git ls-remote --exit-code --tags origin refs/tags/$TagName"
        return $false
    }

    git -C $repositoryDirectory ls-remote `
        --exit-code `
        --tags `
        origin `
        "refs/tags/$TagName" *> $null

    $exitCode = $LASTEXITCODE

    # Empêche le code 2 attendu de perturber la commande suivante.
    $global:LASTEXITCODE = 0

    switch ($exitCode)
    {
        0 {
            return $true
        }

        2 {
            return $false
        }

        default {
            throw "Unable to check whether remote tag '$TagName' exists. Git exited with code $exitCode."
        }
    }
}

function Get-ReleaseJar
{
    param(
        [Parameter(Mandatory = $true)]
        [string] $Directory,

        [Parameter(Mandatory = $true)]
        [string] $Loader,

        [Parameter(Mandatory = $true)]
        [string] $Branch
    )

    $jars = @(
    Get-ChildItem `
            -Path $Directory `
            -Filter "*.jar" |
            Where-Object {
                $_.Name -notmatch "(?i)-(sources|javadoc|dev|shadow|all)\.jar$"
            }
    )

    if ($jars.Count -ne 1)
    {
        $foundFiles = if ($jars.Count -eq 0)
        {
            "none"
        }
        else
        {
            $jars.Name -join ", "
        }

        throw "Expected exactly one distributable $Loader JAR for branch '$Branch', found $( $jars.Count ): $foundFiles"
    }

    return $jars[0]
}

if ($ReleaseBranch -notin $Branches)
{
    throw "Release branch '$ReleaseBranch' must be present in the Branches list."
}

$initialBranch = (
git -C $repositoryDirectory branch --show-current
).Trim()

if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($initialBranch))
{
    throw "Unable to determine the current Git branch."
}

try
{
    if (Test-Path $releaseDirectory)
    {
        Remove-Item $releaseDirectory -Recurse -Force
    }

    New-Item `
        -ItemType Directory `
        -Path $releaseDirectory |
            Out-Null

    foreach ($branch in $Branches)
    {
        Write-Host ""
        Write-Host "========================================"
        Write-Host "Processing Minecraft branch: $branch"
        Write-Host "========================================"

        Invoke-Git @(
            "switch",
            $branch
        )

        if ($branch -eq $ReleaseBranch)
        {
            Write-Host "Building release branch without publishing yet..."

            # Cette branche sera publiée plus tard par announceDiscord,
            # car elle fait partie de setPlatforms(...).
            Invoke-Gradle @(
                "clean",
                ":fabric:jar",
                ":neoforge:jar"
            )
        }
        else
        {
            Write-Host "Building and publishing Modrinth/CurseForge..."

            Invoke-Gradle @(
                "clean",
                ":fabric:jar",
                ":neoforge:jar",
                "publishModrinthFabric",
                "publishCurseforgeFabric",
                "publishModrinthNeoForge",
                "publishCurseforgeNeoForge"
            )
        }

        if ($DryRun)
        {
            Write-Host "DRY RUN: collect Fabric and NeoForge JARs for branch '$branch'"
            continue
        }

        $fabricJar = Get-ReleaseJar `
            -Directory "$repositoryDirectory\fabric\build\libs" `
            -Loader "Fabric" `
            -Branch $branch

        $neoForgeJar = Get-ReleaseJar `
            -Directory "$repositoryDirectory\neoforge\build\libs" `
            -Loader "NeoForge" `
            -Branch $branch

        Copy-Item `
            -Path $fabricJar.FullName `
            -Destination $releaseDirectory

        Copy-Item `
            -Path $neoForgeJar.FullName `
            -Destination $releaseDirectory
    }

    Write-Host ""
    Write-Host "Preparing the final release..."

    Invoke-Git @(
        "switch",
        $ReleaseBranch
    )

    $modVersion = (Get-GradleProperty "mod_version").Split("-", 2)[0]
    $tagName = "v$modVersion"

    Write-Host "Creating Git tag '$tagName' on branch '$ReleaseBranch'..."

    if (Test-LocalGitTagExists -TagName $tagName)
    {
        throw "The local tag '$tagName' already exists."
    }

    if (Test-RemoteGitTagExists -TagName $tagName)
    {
        throw "The remote tag '$tagName' already exists."
    }

    Invoke-Git @(
        "tag",
        $tagName
    )

    Invoke-Git @(
        "push",
        "origin",
        $tagName
    )

    Write-Host ""
    Write-Host "Publishing the release branch and creating the GitHub release..."

    Invoke-Gradle @(
        "publishGithub",
        "-Prelease_jars_dir=$releaseDirectory",
        "-Pgithub_commitish=$ReleaseBranch",
        "-Pgithub_tag_name=$tagName"
    )

    Write-Host ""
    Write-Host "Publishing the release branch to Modrinth/CurseForge and sending the Discord announcement..."

    Invoke-Gradle @(
        "announceDiscord"
    )

    if (Test-Path $releaseDirectory)
    {
        Remove-Item $releaseDirectory -Recurse -Force
    }

    Write-Host ""
    Write-Host "Release successfully published."
}
finally
{
    Write-Host ""
    Write-Host "Returning to branch '$initialBranch'..."

    if ($DryRun)
    {
        Write-Host "DRY RUN: git switch $initialBranch"
    }
    else
    {
        & git -C $repositoryDirectory switch $initialBranch

        if ($LASTEXITCODE -ne 0)
        {
            Write-Warning "Unable to return to initial branch '$initialBranch'."
        }
    }
}