package br.dev.brunoxkk0.airworldborder;

import br.dev.brunoxkk0.airborder.api.AirBorderAPI;
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

    @Override
    public void onEnable() {

        refreshWorlds();

        System.out.println(CACHED_WORLDS);

        AirBorderAPI.setBorderProvider((int x, int z, int dim) -> {

            if(!CACHED_WORLDS.containsKey(dim))
                refreshWorlds();

            if(!CACHED_WORLDS.containsKey(dim))
                return false;

            try{

                System.out.println(CACHED_WORLDS.get(dim).getName());

                boolean a = !WorldBorder.plugin.getWorldBorder(
                        CACHED_WORLDS.get(dim).getName()
                ).insideBorder(new CoordXZ(x << 4, z << 4));
                System.out.println(x + " " + z + " " + dim + " " + a);
                return a;
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
        // Plugin shutdown logic
    }

}
