/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elizauser;

/**
 *
 * @author Robbie
 */
public class CommandReceive extends Thread {

    private CommandControl commandcontrol;
    private ChatController chatcontrol;

    public CommandReceive(CommandControl commandc, ChatController chatc) {
        commandcontrol = commandc;
        chatcontrol = chatc;
    }

    @Override
    public void run() {
        String s;
        while (true) {
            s = commandcontrol.recvString();
            System.out.println(s);
            if (s != null) {
                chatcontrol.handReceive(s);
            }
        }
    }
}
