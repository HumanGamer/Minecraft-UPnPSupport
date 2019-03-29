package com.humangamer.mc.upnpsupport.proxy;

import net.minecraftforge.fml.common.event.*;

public interface IProxy {
    void preInit(FMLPreInitializationEvent event);
    void init(FMLInitializationEvent event);
    void postInit(FMLPostInitializationEvent event);
    void serverStarting(FMLServerStartingEvent event);
    void serverStopping(FMLServerStoppingEvent event);
}
