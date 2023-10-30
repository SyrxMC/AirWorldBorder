package br.dev.brunoxkk0.airworldborder;

import br.dev.brunoxkk0.airborder.api.AirBorderAPI;
import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.CoordXZ;
import com.wimbli.WorldBorder.WorldBorder;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class AirWorldBorder extends JavaPlugin implements Listener {

    private static final HashMap<Integer, World> CACHED_WORLDS = new HashMap<>();
    private static final HashMap<Integer, BorderData> CACHED_WORLD_BORDERS = new HashMap<>();
    private static Long LAST_CHECK = 0L;

    @Override
    public void onEnable() {

        refreshWorlds();

        AirBorderAPI.setBorderProvider((int x, int z, int dim) -> {

            if(!CACHED_WORLDS.containsKey(dim))
                refreshWorlds();

            if(!CACHED_WORLDS.containsKey(dim))
                return false;

            try{

                long lastTime = System.currentTimeMillis();

                if(LAST_CHECK <= lastTime){
                    CACHED_WORLD_BORDERS.remove(dim);
                    LAST_CHECK = lastTime + 20000;
                }

                if(!CACHED_WORLD_BORDERS.containsKey(dim))
                    CACHED_WORLD_BORDERS.put(dim, WorldBorder.plugin.getWorldBorder(CACHED_WORLDS.get(dim).getName()));

                BorderData data = CACHED_WORLD_BORDERS.get(dim);

                if(data != null){
                    return data.insideBorder(new CoordXZ(x << 4, z << 4));
                }

            }catch (Exception ignored){
                System.out.println(ignored.getMessage());
            }

            return false;
        });

    }

    private void refreshWorlds() {
        for(World world : Bukkit.getWorlds()){
            WorldServer mcWorld = ((CraftWorld) world).getHandle();
            CACHED_WORLDS.put(mcWorld.worldProvider.dimension, world);
        }
    }

    @Override
    public void onDisable() {
        CACHED_WORLDS.clear();
        CACHED_WORLD_BORDERS.clear();
    }

}
