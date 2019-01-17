package gyurix.protocol.wrappers.outpackets;

import gyurix.protocol.Reflection;
import gyurix.protocol.event.PacketOutType;
import gyurix.protocol.utils.DataWatcher;
import gyurix.protocol.utils.GameProfile;
import gyurix.protocol.wrappers.WrappedPacket;
import gyurix.spigotutils.LocationData;

import java.util.UUID;

import static gyurix.spigotutils.ServerVersion.*;

/**
 * Created by GyuriX on 2016.03.06..
 */
public class PacketPlayOutNamedEntitySpawn extends WrappedPacket {
    public GameProfile profile;
    public int entityId, handItemId;
    public UUID entityUUID;
    public DataWatcher meta;
    public double x, y, z;
    public byte yaw, pitch;

    public PacketPlayOutNamedEntitySpawn() {

    }

    public PacketPlayOutNamedEntitySpawn(int eid, GameProfile profile, LocationData loc, DataWatcher data) {
        entityId = eid;
        entityUUID = profile.id;
        this.profile = profile;
        setLocation(loc);
        meta = data;
    }

    public PacketPlayOutNamedEntitySpawn(int eid, UUID eUUID, LocationData loc, DataWatcher data) {
        entityId = eid;
        entityUUID = eUUID;
        setLocation(loc);
        meta = data;
    }

    @Override
    public Object getVanillaPacket() {
        if (Reflection.ver.isAbove(v1_9))
            return PacketOutType.NamedEntitySpawn.newPacket(entityId, entityUUID, x, y, z, yaw, pitch, meta.toNMS());
        else if (Reflection.ver.isAbove(v1_8))
            return PacketOutType.NamedEntitySpawn.newPacket(entityId, entityUUID,
                    (int) (x * 32), (int) (y * 32), (int) (z * 32), yaw, pitch, meta.toNMS());
        else
            return PacketOutType.NamedEntitySpawn.newPacket(entityId, profile.toNMS(),
                    (int) (x * 32), (int) (y * 32), (int) (z * 32), yaw, pitch, meta.toNMS());

    }

    @Override
    public void loadVanillaPacket(Object packet) {
        Object[] d = PacketOutType.NamedEntitySpawn.getPacketData(packet);
        entityId = (int) d[0];
        if (Reflection.ver.isBellow(v1_7)) {
            profile = new GameProfile(d[1]);
            entityUUID = profile.id;
        } else
            entityUUID = (UUID) d[1];
        if (Reflection.ver.isAbove(v1_9)) {
            x = (double) d[2];
            y = (double) d[3];
            z = (double) d[4];
        } else {
            x = (int) d[2] / 32.0;
            y = (int) d[3] / 32.0;
            z = (int) d[4] / 32.0;
            handItemId = (int) d[7];
        }
        yaw = (byte) d[5];
        pitch = (byte) d[6];
        meta = new DataWatcher(d[d.length - 2]);
    }

    public void setLocation(LocationData loc) {
        x = loc.x;
        y = loc.y;
        z = loc.z;
        while (loc.yaw <= -180)
            loc.yaw += 360;
        while (loc.yaw >= 180)
            loc.yaw -= 360;
        yaw = (byte) (loc.yaw * 256 / 360);
        pitch = (byte) (loc.pitch * 256 / 360);
    }
}
