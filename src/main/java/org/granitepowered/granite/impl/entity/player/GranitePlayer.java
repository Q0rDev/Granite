/*
 * License (MIT)
 *
 * Copyright (c) 2014-2015 Granite Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.granitepowered.granite.impl.entity.player;

import static org.granitepowered.granite.util.MinecraftUtils.graniteToMinecraftChatComponent;
import static org.granitepowered.granite.util.MinecraftUtils.unwrap;
import static org.granitepowered.granite.util.MinecraftUtils.wrap;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.NotImplementedException;
import org.granitepowered.granite.impl.effect.particle.GraniteParticleEffect;
import org.granitepowered.granite.impl.effect.particle.GraniteParticleType;
import org.granitepowered.granite.impl.item.GraniteItemBlock;
import org.granitepowered.granite.impl.item.GraniteItemType;
import org.granitepowered.granite.impl.text.chat.GraniteChatType;
import org.granitepowered.granite.impl.text.message.GraniteMessage;
import org.granitepowered.granite.impl.text.message.GraniteMessageBuilder;
import org.granitepowered.granite.mappings.Mappings;
import org.granitepowered.granite.mc.MCEntity;
import org.granitepowered.granite.mc.MCEntityPlayerMP;
import org.granitepowered.granite.mc.MCItemStack;
import org.granitepowered.granite.mc.MCPacket;
import org.granitepowered.granite.mc.MCPacketTitleType;
import org.granitepowered.granite.mc.MCRegistryNamespaced;
import org.granitepowered.granite.util.Instantiator;
import org.granitepowered.granite.util.MinecraftUtils;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.player.gamemode.GameMode;
import org.spongepowered.api.entity.projectile.Arrow;
import org.spongepowered.api.entity.projectile.Egg;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.fireball.LargeFireball;
import org.spongepowered.api.entity.projectile.fireball.SmallFireball;
import org.spongepowered.api.item.ItemBlock;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.net.PlayerConnection;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.context.Context;
import org.spongepowered.api.service.persistence.DataSource;
import org.spongepowered.api.service.persistence.data.DataContainer;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.message.Message;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.text.title.Titles;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.command.CommandSource;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

public class GranitePlayer extends GraniteEntityPlayer<MCEntityPlayerMP> implements Player {
    private Optional<Message> displayName = Optional.absent();

    public GranitePlayer(MCEntityPlayerMP obj) {
        super(obj);
    }

    @Override
    public Message getDisplayName() {
        return displayName.or(new GraniteMessageBuilder.GraniteTextMessageBuilder("").content(getName()).build());
    }

    @Override
    public boolean getAllowFlight() {
        return obj.fieldGet$capabilities().fieldGet$allowFlying();
    }

    @Override
    public void setAllowFlight(boolean allowFlight) {
        obj.fieldGet$capabilities().fieldSet$allowFlying(allowFlight);
    }

    @Override
    public Locale getLocale() {
        String translator = this.obj.fieldGet$translator().replace("_", "-");
        return Locale.forLanguageTag(translator);
    }

    @Override
    public void sendMessage(ChatType type, String... strings) {
        List<Message> messages = new ArrayList<>();
        for (String string : strings) {
            messages.add(new GraniteMessageBuilder.GraniteTextMessageBuilder("").content(string).build());
        }

        sendMessage(type, messages);
    }

    @Override
    public void sendTitle(Title title) {
        if (title.isReset() || title.isClear()) {
            MCPacketTitleType
                    type =
                    (MCPacketTitleType) MinecraftUtils.enumValue(Mappings.getClass("S45PacketTitle$Type"), title.isReset() ? 4 : 3);

            MCPacket packet = Instantiator.get().newPacketTitle(type, null);
            obj.fieldGet$playerNetServerHandler().sendPacket(packet);
        }

        if (title.getFadeIn().isPresent() || title.getStay().isPresent() || title.getFadeOut().isPresent()) {
            int fadeIn = title.getFadeIn().or(-1);
            int stay = title.getFadeIn().or(-1);
            int fadeOut = title.getFadeIn().or(-1);

            MCPacket packet = Instantiator.get().newPacketTitle(fadeIn, stay, fadeOut);
            obj.fieldGet$playerNetServerHandler().sendPacket(packet);
        }

        if (title.getTitle().isPresent()) {
            MCPacketTitleType type = (MCPacketTitleType) MinecraftUtils.enumValue(Mappings.getClass("S45PacketTitle$Type"), 0);

            MCPacket packet = Instantiator.get().newPacketTitle(type, MinecraftUtils.graniteToMinecraftChatComponent(title.getTitle().get()));
            obj.fieldGet$playerNetServerHandler().sendPacket(packet);
        }

        if (title.getTitle().isPresent()) {
            MCPacketTitleType type = (MCPacketTitleType) MinecraftUtils.enumValue(Mappings.getClass("S45PacketTitle$Type"), 1);

            MCPacket packet = Instantiator.get().newPacketTitle(type, MinecraftUtils.graniteToMinecraftChatComponent(title.getSubtitle().get()));
            obj.fieldGet$playerNetServerHandler().sendPacket(packet);
        }
    }

    @Override
    public void resetTitle() {
        sendTitle(Titles.update().reset().build());
    }

    @Override
    public void clearTitle() {
        sendTitle(Titles.update().clear().build());
    }

    @Override
    public GameMode getGameMode() {
        throw new NotImplementedException("");
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        throw new NotImplementedException("");
    }

    @Override
    public PlayerConnection getConnection() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<ItemStack> getHelmet() {
        return Optional.fromNullable((ItemStack) wrap(obj.fieldGet$inventory().fieldGet$armorInventory()[3]));
    }

    @Override
    public void setHelmet(@Nullable ItemStack helmet) {
        obj.fieldGet$inventory().fieldGet$armorInventory()[3] = helmet == null ? null : (MCItemStack) unwrap(helmet);
    }

    @Override
    public Optional<ItemStack> getChestplate() {
        return Optional.fromNullable((ItemStack) wrap(obj.fieldGet$inventory().fieldGet$armorInventory()[2]));
    }

    @Override
    public void setChestplate(@Nullable ItemStack chestplate) {
        obj.fieldGet$inventory().fieldGet$armorInventory()[2] = chestplate == null ? null : (MCItemStack) unwrap(chestplate);
    }

    @Override
    public Optional<ItemStack> getLeggings() {
        return Optional.fromNullable((ItemStack) wrap(obj.fieldGet$inventory().fieldGet$armorInventory()[1]));
    }

    @Override
    public void setLeggings(@Nullable ItemStack leggings) {
        obj.fieldGet$inventory().fieldGet$armorInventory()[1] = leggings == null ? null : (MCItemStack) unwrap(leggings);
    }

    @Override
    public Optional<ItemStack> getBoots() {
        return Optional.fromNullable((ItemStack) wrap(obj.fieldGet$inventory().fieldGet$armorInventory()[0]));
    }

    @Override
    public void setBoots(@Nullable ItemStack boots) {
        obj.fieldGet$inventory().fieldGet$armorInventory()[0] = boots == null ? null : (MCItemStack) unwrap(boots);
    }

    @Override
    public Optional<ItemStack> getItemInHand() {
        return Optional.fromNullable((ItemStack) wrap(obj.fieldGet$inventory().fieldGet$mainInventory()[obj.fieldGet$inventory().fieldGet$currentItem()]));
    }

    @Override
    public void setItemInHand(@Nullable ItemStack itemInHand) {
        MCItemStack[] itemStack = obj.fieldGet$inventory().fieldGet$mainInventory();
        itemStack[obj.fieldGet$inventory().fieldGet$currentItem()] = unwrap(itemInHand);
    }

    @Override
    public GameProfile getProfile() {
        return wrap(obj.fieldGet$gameProfile());
    }

    @Override
    public String getName() {
        return getProfile().getName();
    }

    @Override
    public boolean hasJoinedBefore() {
        // TODO: Not sure if possible
        throw new NotImplementedException("");
    }

    @Override
    public Date getFirstPlayed() {
        // TODO: Not sure if possible
        throw new NotImplementedException("");
    }

    @Override
    public Date getLastPlayed() {
        // TODO: Not sure if possible
        throw new NotImplementedException("");
    }

    @Override
    public boolean isBanned() {
        // TODO: Check SCM.bannedPlayers
        throw new NotImplementedException("");
    }

    @Override
    public boolean isWhitelisted() {
        // TODO: Check SCM.whiteListedPlayers
        throw new NotImplementedException("");
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public Optional<Player> getPlayer() {
        return Optional.of((Player) this);
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position) {
        spawnParticles(particleEffect, position, 255);
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position, int radius) {
        // TODO: Radius is currently ignored, fix this
        GraniteParticleType type = (GraniteParticleType) particleEffect.getType();

        Vector3f offset = particleEffect.getOffset();

        Enum internal = (Enum) Mappings.getClass("EnumParticleTypes").getEnumConstants()[type.getId()];

        int count = particleEffect.getCount();
        int[] extra = new int[0];

        float px = (float) position.getX();
        float py = (float) position.getY();
        float pz = (float) position.getZ();

        float ox = offset.getX();
        float oy = offset.getY();
        float oz = offset.getZ();

        // The extra values, normal behavior offsetX, offsetY, offsetZ
        float f0 = 0f;
        float f1 = 0f;
        float f2 = 0f;

        // Depends on behavior
        // Note: If the count > 0 -> speed = 0f else if count = 0 -> speed = 1f

        if (particleEffect instanceof GraniteParticleEffect.GraniteMaterial) {
            ItemStack item = ((GraniteParticleEffect.GraniteMaterial) particleEffect).getItem();
            ItemType itemType = item.getItem();

            int id = 0;
            int data = 0;

            if (type.getId() == ((GraniteParticleType) ParticleTypes.ITEM_CRACK).getId()) {
                try {
                    id =
                            ((MCRegistryNamespaced) Mappings.getField("Item", "itemRegistry").get(null))
                                    .getIDForObject(((GraniteItemType) itemType).obj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                data = item.getDamage();
            } else if (type.getId() == ((GraniteParticleType) ParticleTypes.BLOCK_CRACK).getId()
                       || type.getId() == ((GraniteParticleType) ParticleTypes.BLOCK_DUST).getId()) {
                // Only block types are allowed
                if (itemType instanceof ItemBlock) {
                    try {
                        id =
                                ((MCRegistryNamespaced) Mappings.getField("Block", "blockRegistry").get(null))
                                        .getIDForObject(((GraniteItemBlock) itemType).obj.fieldGet$block());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    data = item.getDamage();
                }
            }

            if (id == 0) {
                return;
            }

            extra = new int[]{id, data};
        }

        if (particleEffect instanceof GraniteParticleEffect.GraniteResizable) {
            float size = ((GraniteParticleEffect.Resizable) particleEffect).getSize();

            // The formula of the large explosion acts strange [business as usual, then.]
            // Client formula: sizeClient = 1 - sizeServer * 0.5
            // The particle effect returns the client value so
            // Server formula: sizeServer = (-sizeClient * 2) + 2
            if (type.getId() == ((GraniteParticleType) ParticleTypes.EXPLOSION_LARGE).getId()) {
                size = (-size * 2f) + 2f;
            }

            if (size == 0f) {
                obj.fieldGet$playerNetServerHandler()
                        .sendPacket(Instantiator.get().newPacketParticles(internal, true, px, py, pz, ox, oy, oz, 0f, count, extra));
                return;
            }

            f0 = size;
        } else if (particleEffect instanceof GraniteParticleEffect.GraniteColorable) {
            Color color0 = ((GraniteParticleEffect.Colorable) particleEffect).getColor();
            Color color1 = ((GraniteParticleType.Colorable) type).getDefaultColor();

            if (color0.equals(color1)) {
                obj.fieldGet$playerNetServerHandler()
                        .sendPacket(Instantiator.get().newPacketParticles(internal, true, px, py, pz, ox, oy, oz, 0f, count, extra));
                return;
            }

            f0 = color0.getRed() / 255f;
            f1 = color0.getGreen() / 255f;
            f2 = color0.getBlue() / 255f;

            // If the f0 value 0 is, the redstone will set it automatically to red 255
            if (f0 == 0f && type.getId() == ((GraniteParticleType) ParticleTypes.REDSTONE).getId()) {
                f0 = 0.00001f;
            }
        } else if (particleEffect instanceof GraniteParticleEffect.Note) {
            float note = ((GraniteParticleEffect.Note) particleEffect).getNote();

            if (note == 0f) {
                obj.fieldGet$playerNetServerHandler()
                        .sendPacket(Instantiator.get().newPacketParticles(internal, true, px, py, pz, ox, oy, oz, 0f, count, extra));
                return;
            }

            f0 = note / 24f;
        } else if (type.hasMotion()) {
            Vector3f motion = particleEffect.getMotion();

            float mx = motion.getX();
            float my = motion.getY();
            float mz = motion.getZ();

            // The y value won't work for this effect, if the value isn't 0 the motion won't work
            if (type.getId() == ((GraniteParticleType) ParticleTypes.WATER_SPLASH).getId()) {
                my = 0f;
            }

            if (mx == 0f && my == 0f && mz == 0f) {
                obj.fieldGet$playerNetServerHandler()
                        .sendPacket(Instantiator.get().newPacketParticles(internal, true, px, py, pz, ox, oy, oz, 0f, count, extra));
                return;
            } else {
                f0 = mx;
                f1 = my;
                f2 = mz;
            }
        }

        // Is this check necessary?
        if (f0 == 0f && f1 == 0f && f2 == 0f) {
            obj.fieldGet$playerNetServerHandler()
                    .sendPacket(Instantiator.get().newPacketParticles(internal, true, px, py, pz, ox, oy, oz, 0f, count, extra));
            return;
        }

        List<MCPacket> packets = Lists.newArrayList();

        if (ox == 0f && oy == 0f && oz == 0f) {
            for (int i = 0; i < count; i++) {
                packets.add(Instantiator.get().newPacketParticles(internal, true, px, py, pz, f0, f1, f2, 1f, 0, extra));
            }
        } else {
            Random random = new Random();

            for (int i = 0; i < count; i++) {
                float px0 = px + (random.nextFloat() * 2f - 1f) * ox;
                float py0 = py + (random.nextFloat() * 2f - 1f) * oy;
                float pz0 = pz + (random.nextFloat() * 2f - 1f) * oz;

                packets.add(Instantiator.get().newPacketParticles(internal, true, px0, py0, pz0, f0, f1, f2, 1f, 0, extra));
            }
        }

        for (MCPacket packet : packets) {
            obj.fieldGet$playerNetServerHandler().sendPacket(packet);
        }
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume) {
        // TODO: Make World call Player and not the reverse, so you can send a sound to an individual player
        this.getWorld().playSound(sound, position, volume, 1.0F);
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume, double pitch) {
        // TODO: Make World call Player and not the reverse, so you can send a sound to an individual player
        this.getWorld().playSound(sound, position, volume, pitch);
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume, double pitch, double minVolume) {
        // TODO: Make World call Player and not the reverse, so you can send a sound to an individual player
        this.getWorld().playSound(sound, position, volume, pitch, minVolume);
    }

    @Override
    public float getHunger() {
        return obj.fieldGet$foodStats().fieldGet$foodLevel();
    }

    @Override
    public void setHunger(float hunger) {
        obj.fieldGet$foodStats().fieldSet$foodLevel((int) hunger);
    }

    @Override
    public float getSaturation() {
        return obj.fieldGet$foodStats().fieldGet$foodSaturationLevel();
    }

    @Override
    public void setSaturation(float saturation) {
        obj.fieldGet$foodStats().fieldSet$foodSaturationLevel(saturation);
    }

    @Override
    public double getExperience() {
        return obj.fieldGet$experience();
    }

    @Override
    public int getLevel() {
        return obj.fieldGet$experienceLevel();
    }

    @Override
    public double getTotalExperinece() {
        return obj.fieldGet$experienceTotal();
    }

    @Override
    public void setExperience(double experience) {
        obj.fieldSet$experience((float) experience);
    }

    @Override
    public void setLevel(int level) {
        obj.fieldSet$experienceLevel(level);
    }

    @Override
    public void setTotalExperience(double totalExperience) {
        obj.fieldSet$experienceTotal((int) totalExperience);
    }

    @Override
    public boolean isViewingInventory() {
        return obj.fieldGet$openContainer() != null;
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<T> projectileClass) {
        MCEntity entity = null;
        if (projectileClass.isAssignableFrom(Arrow.class)) {
            entity = Instantiator.get().newEntityArrow(obj.fieldGet$worldObj(), obj, 2);
        } else if (projectileClass.isAssignableFrom(Egg.class)) {
            entity = Instantiator.get().newEntityEgg(obj.fieldGet$worldObj(), obj);
        } else if (projectileClass.isAssignableFrom(SmallFireball.class)) {
            entity = Instantiator.get().newEntitySmallFireball(obj.fieldGet$worldObj(), obj, 0, 0, 0);
            entity.setPositionAndUpdate(getEyeLocation().getX(), getEyeLocation().getY(), getEyeLocation().getZ());
        } else if (projectileClass.isAssignableFrom(LargeFireball.class)) {
            entity = Instantiator.get().newEntityLargeFireball(obj.fieldGet$worldObj(), obj, 0, 0, 0);
            entity.setPositionAndUpdate(getEyeLocation().getX(), getEyeLocation().getY(), getEyeLocation().getZ());
        } else {
            throw new NotImplementedException("");
        }
        obj.fieldGet$worldObj().spawnEntityInWorld(entity);
        return (T) wrap(entity);
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<T> projectileClass, Vector3f velocity) {
        // MCEntity e = unwrap(launchProjectile(projectileClass));
        // TODO: Set speed on projectile
        throw new NotImplementedException("");
    }

    @Override
    public void sendMessage(String... messages) {
        sendMessage(ChatTypes.CHAT, messages);
    }

    @Override
    public void sendMessage(Iterable<Message> messages) {
        sendMessage(ChatTypes.CHAT, messages);
    }

    @Override
    public void sendMessage(Message... messages) {
        sendMessage(ChatTypes.CHAT, messages);
    }

    @Override
    public void sendMessage(ChatType type, Message... message) {
        sendMessage(type, Arrays.asList(message));
    }

    @Override
    public void sendMessage(ChatType type, Iterable<Message> messages) {
        Message message;
        if (messages instanceof GraniteMessage) {
            message = (Message) messages;
        } else {
            message = new GraniteMessageBuilder.GraniteTextMessageBuilder("").content("").append(messages).build();
        }

        MCPacket packet = Instantiator.get().newPacketChat(graniteToMinecraftChatComponent(message), (byte) ((GraniteChatType) type).getId());
        obj.fieldGet$playerNetServerHandler().sendPacket(packet);
    }

    public DataContainer toContainer() {
        // TODO: Persistence API
        throw new NotImplementedException("");
    }

    @Override
    public void serialize(DataSource source) {
        // TODO: Persistence API
        throw new NotImplementedException("");
    }

    @Override
    public String getIdentifier() {
        // TODO: Permissions API
        throw new NotImplementedException("");
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        // TODO: Permissions API
        throw new NotImplementedException("");
    }

    @Override
    public SubjectCollection getContainingCollection() {
        // TODO: Permissions API
        throw new NotImplementedException("");
    }

    @Override
    public SubjectData getData() {
        // TODO: Permissions API
        throw new NotImplementedException("");
    }

    @Override
    public SubjectData getTransientData() {
        // TODO: Permissions API
        throw new NotImplementedException("");
    }

    @Override
    public boolean hasPermission(Set<Context> contexts, String permission) {
        // TODO: Permissions API
        throw new NotImplementedException("");
    }

    @Override
    public boolean hasPermission(String permission) {
        // TODO: Permissions API
        throw new NotImplementedException("");
    }

    @Override
    public Tristate getPermissionValue(Set<Context> contexts, String permission) {
        // TODO: Permissions API
        throw new NotImplementedException("");
    }

    @Override
    public boolean isChildOf(Subject parent) {
        // TODO: Permissions API
        throw new NotImplementedException("");
    }

    @Override
    public boolean isChildOf(Set<Context> contexts, Subject parent) {
        // TODO: Permissions API
        throw new NotImplementedException("");
    }

    @Override
    public List<Subject> getParents() {
        // TODO: Permissions API
        throw new NotImplementedException("");
    }

    @Override
    public List<Subject> getParents(Set<Context> contexts) {
        // TODO: Permissions API
        throw new NotImplementedException("");
    }

    @Override
    public Set<Context> getActiveContexts() {
        // TODO: Permissions API
        throw new NotImplementedException("");
    }
}
