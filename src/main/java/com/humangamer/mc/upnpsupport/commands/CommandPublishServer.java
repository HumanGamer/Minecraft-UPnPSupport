package com.humangamer.mc.upnpsupport.commands;

import com.dosse.upnp.UPnP;
import com.humangamer.mc.upnpsupport.UPnPSupport;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;

public class CommandPublishServer extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getName()
    {
        return "open";
    }

    /**
     * Gets the usage string for the command.
     */
    public String getUsage(ICommandSender sender)
    {
        return "/open <port> [cheats=false] [gameMode=Survival]";
    }

    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (Minecraft.getMinecraft()== null || Minecraft.getMinecraft().getIntegratedServer() == null)
        {
            sender.sendMessage(format(TextFormatting.RED, "Only the host may use this command!"));
            return;
        }

        if (sender.getCommandSenderEntity() != null && sender.getCommandSenderEntity() instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP)sender.getCommandSenderEntity();

            if (Minecraft.getMinecraft().player.getUniqueID() != player.getUniqueID())
            {
                sender.sendMessage(format(TextFormatting.RED, "Only the host may use this command!"));
                return;
            }
        }

        if (args.length == 0)
        {
            sender.sendMessage(format(TextFormatting.RED, "Usage: " + getUsage(sender)));
            return;
        }

        if (Minecraft.getMinecraft().getIntegratedServer().isPublic)
        {
            sender.sendMessage(format(TextFormatting.RED, "Failed to open server: Already open"));
            return;
        }

        if (!UPnP.isUPnPAvailable())
        {
            sender.sendMessage(format(TextFormatting.RED, "Failed to open server: Could not find a UPnP-enabled router"));
            return;
        }

        GameType gameType = GameType.SURVIVAL;
        boolean cheats = false;
        int port;

        String portString = args[0];
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException ex)
        {
            sender.sendMessage(format(TextFormatting.RED, "Failed to open server: Invalid Port: " + portString));

            return;
        }

        if (args.length > 1)
        {
            String cheatsString = args[1];

            cheats = Boolean.parseBoolean(cheatsString);
        }

        if (args.length > 2)
        {
            String typeString = args[2];

            GameType gametype = GameType.parseGameTypeWithDefault(typeString, GameType.NOT_SET);
            gameType = (gametype == GameType.NOT_SET ? WorldSettings.getGameTypeById(parseInt(typeString, 0, GameType.values().length - 2)) : gametype);
        }

        String s = UPnPSupport.shareToLAN(gameType, cheats, port);

        if (s != null)
        {

            sender.sendMessage(format(TextFormatting.GREEN, "Opened server: " + s));
        }
        else
        {
            sender.sendMessage(format(TextFormatting.RED, "Failed to open server"));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    private TextComponentTranslation format(TextFormatting color, String str, Object... args)
    {
        TextComponentTranslation ret = new TextComponentTranslation(str, args);
        ret.getStyle().setColor(color);
        return ret;
    }
}