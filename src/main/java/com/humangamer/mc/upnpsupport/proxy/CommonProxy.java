package com.humangamer.mc.upnpsupport.proxy;

import com.dosse.upnp.UPnP;
import com.humangamer.mc.upnpsupport.UPnPSupport;
import com.humangamer.mc.upnpsupport.commands.CommandPublishServer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.event.*;

public class CommonProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        UPnPSupport.instance.logger.info("Waiting for UPnP Initialization");
        UPnP.waitInit();
        UPnPSupport.instance.logger.info("Done UPnP Initialization.");
        ClientCommandHandler.instance.registerCommand(new CommandPublishServer());
    }

    @Override
    public void init(FMLInitializationEvent event)
    {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {

    }

    @Override
    public void serverStarting(FMLServerStartingEvent event)
    {

    }

    @Override
    public void serverStopping(FMLServerStoppingEvent event)
    {
        if (UPnPSupport.instance.openedPortUDP) {
            if (UPnP.closePortUDP(UPnPSupport.instance.portNum))
            {
                UPnPSupport.instance.openedPortUDP = false;
                UPnPSupport.instance.logger.info("Closed UDP port: " + UPnPSupport.instance.portNum);
            } else
            {
                UPnPSupport.instance.logger.warn("Failed to close UDP port: " + UPnPSupport.instance.portNum);
            }
        }

        if (UPnPSupport.instance.openedPortTCP) {
            if (UPnP.closePortTCP(UPnPSupport.instance.portNum))
            {
                UPnPSupport.instance.openedPortTCP = false;
                UPnPSupport.instance.logger.info("Closed TCP port: " + UPnPSupport.instance.portNum);
            } else
            {
                UPnPSupport.instance.logger.warn("Failed to close UDP port: " + UPnPSupport.instance.portNum);
            }
        }
    }

}
