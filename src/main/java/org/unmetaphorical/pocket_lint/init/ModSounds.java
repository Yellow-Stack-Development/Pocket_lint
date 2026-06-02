package org.unmetaphorical.pocket_lint.init;

import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.unmetaphorical.pocket_lint.Pocket_lint;

public class ModSounds {
    public static final SoundEvent SOAPY_MUSIC = registerSound("soapy_music");
    public static final RegistryKey<JukeboxSong> SOAPY_MUSIC_KEY = RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(Pocket_lint.MOD_ID, "soapy_music"));

    private static SoundEvent registerSound(String name) {
        Identifier id = Identifier.of(Pocket_lint.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        Pocket_lint.LOGGER.info("Registering sounds for " + Pocket_lint.MOD_ID);
    }
}
