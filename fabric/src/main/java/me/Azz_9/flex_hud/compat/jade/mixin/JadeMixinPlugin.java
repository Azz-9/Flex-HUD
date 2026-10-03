package me.Azz_9.flex_hud.compat.jade.mixin;

import static me.Azz_9.flex_hud.Constants.JADE_ID;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

import me.Azz_9.flex_hud.platform.Services;

public class JadeMixinPlugin implements IMixinConfigPlugin {

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return Services.PLATFORM.isModLoaded(JADE_ID);
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}
}
