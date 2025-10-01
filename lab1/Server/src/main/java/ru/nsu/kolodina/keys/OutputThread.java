package ru.nsu.kolodina.keys;

import lombok.AllArgsConstructor;
import ru.nsu.kolodina.keys.entity.ClientConnection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

@AllArgsConstructor
public class OutputThread implements Runnable {
    final LinkedBlockingQueue<ClientConnection> outputQueue;
    final Selector outputSelector;

    public void writeToClient(SelectionKey key) {
        if (key.isWritable()) {
            System.out.println("Writing in output thread");
            ClientConnection c = (ClientConnection) key.attachment();
            ByteBuffer buf = ByteBuffer.wrap((JsonHandler.createJson(c.rsaKey).toString() + "\n").getBytes(StandardCharsets.UTF_8));
            SocketChannel ch = (SocketChannel) key.channel();
            while (buf.hasRemaining()) {
                int written = 0;
                try {
                    if (!ch.isConnected()) {
                        break;
                    }
                    written = ch.write(buf);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (written == 0) {
                    key.interestOps(SelectionKey.OP_WRITE);
                    return;
                }
            }

            System.out.println("fully written for " + c.name);
            try {
                ch.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                outputSelector.select();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Iterator<SelectionKey> iter = outputSelector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isWritable()) {
                    writeToClient(key);
                    key.cancel();
                }
            }

            ClientConnection c;
            while ((c = outputQueue.poll()) != null) {
                SocketChannel clientChannel = c.clientChannel;
                System.out.println("Client received in output thread");
                try {
                    if (clientChannel.isOpen() && clientChannel.isConnected()) {
                        clientChannel.register(outputSelector, SelectionKey.OP_WRITE).attach(c);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            outputSelector.wakeup();
        }
    }
}
