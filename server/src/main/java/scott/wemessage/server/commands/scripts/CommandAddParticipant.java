/*
 *  weMessage - iMessage for Android
 *  Copyright (C) 2018 Roman Scott
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package scott.wemessage.server.commands.scripts;

import java.util.List;

import scott.wemessage.server.ServerLogger;
import scott.wemessage.server.commands.CommandManager;
import scott.wemessage.server.messages.chat.GroupChat;

public class CommandAddParticipant extends ScriptCommand {

    public CommandAddParticipant(CommandManager manager){
        super(manager, "addparticipant", "Adds a person to a group chat", new String[]{ "addperson", "groupadd", "addaccount" });
    }

    public void execute(String[] args){
        if (args.length < 2){
            ServerLogger.log("Too little arguments provided to run this command.");
            ServerLogger.log("Example Usage: addparticipant \"Group Name\" \"account@icloud.com\"");
            return;
        }

        try {
            List<GroupChat> chats = getMessagesDatabase().getGroupChatsByName(args[0]);

            if (chats.size() == 0) {
                ServerLogger.log("Group Chat " + args[0] + " does not exist!");
                return;
            }

            if (args.length == 2) {
                if (chats.size() == 1){
                    Object result = getScriptExecutor().runAddParticipantScript(chats.get(0), args[1]);

                    ServerLogger.log("Action Add Participant returned a result of: " + processResult(result));
                    return;
                }

                if (chats.size() > 1){
                    ServerLogger.log("Multiple group chats have the name: " + args[0] + "!");
                    ServerLogger.log("Please choose which group chat you want to perform this action on!");
                    ServerLogger.log("Example Usage: addparticipant \"Group Name\" 2 \"account@icloud.com\"");
                    ServerLogger.emptyLine();
                    printChatOptions(chats);
                }
            } else if (args.length == 3) {
                Integer i;

                if (chats.size() == 1){
                    ServerLogger.log("There were too many arguments provided. Make sure your arguments are surrounded in \"quotation marks.\"");
                    ServerLogger.log("Example Usage: addparticipant \"Group Name\" \"account@icloud.com\"");
                    return;
                }

                try {
                    i = Integer.parseInt(args[1]);
                }catch (Exception ex){
                    ServerLogger.log(args[1] + " is not a number! Make sure you choose a valid option based off the list below.");
                    ServerLogger.emptyLine();
                    printChatOptions(chats);
                    return;
                }

                GroupChat chat;

                try {
                    chat = chats.get(i - 1);
                }catch (Exception ex){
                    ServerLogger.log(args[1] + " is not a valid number for your chat list options!");
                    ServerLogger.emptyLine();
                    printChatOptions(chats);
                    return;
                }

                Object result = getScriptExecutor().runAddParticipantScript(chat, args[2]);
                ServerLogger.log("Action Add Participant returned a result of: " + processResult(result));
            } else if (args.length > 3){
                ServerLogger.log("There were too many arguments provided. Make sure your arguments are surrounded in \"quotation marks.\"");
                ServerLogger.log("Example Usage: addparticipant \"Group Name\" \"account@icloud.com\"");
                ServerLogger.emptyLine();

                ServerLogger.log("If you have multiple group chats with the same name, choose your chat based off of the option list.");
                ServerLogger.log("Example Usage: addparticipant \"Group Name\" 3 \"account@icloud.com\"");

                if (chats.size() > 1) {
                    ServerLogger.emptyLine();
                    printChatOptions(chats);
                }
            }
        }catch (Exception ex){
            ServerLogger.error("An error occurred while trying to run this command!", ex);
        }
    }
}