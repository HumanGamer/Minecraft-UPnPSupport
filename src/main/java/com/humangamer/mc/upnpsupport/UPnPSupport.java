package com.humangamer.mc.upnpsupport;

import com.dosse.upnp.UPnP;
import com.humangamer.mc.upnpsupport.proxy.IProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;

@Mod(modid = UPnPSupport.MODID, name = UPnPSupport.NAME, version = UPnPSupport.VERSION, clientSideOnly = true, acceptableRemoteVersions = "*")
public class UPnPSupport
{
    public static final String MODID = "upnpsupport";
    public static final String NAME = "UPnP Support";
    public static final String VERSION = "1.0";

    public static Logger logger;

    public static boolean openedPortUDP;
    public static boolean openedPortTCP;
    public static int portNum;

    @Mod.Instance
    public static UPnPSupport instance;

    @SidedProxy(clientSide = "com.humangamer.mc.upnpsupport.proxy.ClientProxy", serverSide = "com.humangamer.mc.upnpsupport.proxy.ServerProxy")
    public static IProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) { proxy.serverStarting(event); }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
        proxy.serverStopping(event);
    }

    public static String shareToLAN(GameType type, boolean allowCheats, int port)
    {
        if (!UPnP.isUPnPAvailable())
            return null;

        Minecraft mc = Minecraft.getMinecraft();
        IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();

        try
        {
            int i = port;

            if (i <= 0)
            {
                i = 25564;
            }

            boolean openedPortUDP = UPnP.isMappedUDP(i);
            boolean openedPortTCP = UPnP.isMappedTCP(i);

            if (!openedPortUDP)
                openedPortUDP = UPnP.openPortUDP(i);

            if (!openedPortTCP)
                openedPortTCP = UPnP.openPortTCP(i);

            if (!openedPortUDP || !openedPortTCP)
                logger.warn("Failed to open port: " + i);
            else {
                logger.info("Opened port: " + i);
                UPnPSupport.openedPortUDP = true;
                UPnPSupport.openedPortTCP = true;
                UPnPSupport.portNum = i;
            }

            String externalIP = UPnP.getExternalIP();

            server.getNetworkSystem().addLanEndpoint((InetAddress)null, i);
            logger.info("Started server on {}", (int)i);
            server.isPublic = true;
            server.lanServerPing = new ThreadLanServerPing(server.getMOTD(), i + "");
            server.lanServerPing.start();
            server.getPlayerList().setGameType(type);
            server.getPlayerList().setCommandsAllowedForAll(allowCheats);
            if (mc.player.getPermissionLevel() < 4)
                mc.player.setPermissionLevel(allowCheats ? 4 : 0);
            return externalIP + ":" + i;
        }
        catch (IOException var6)
        {
            return null;
        }
    }
}
