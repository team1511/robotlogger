/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotlogger;

import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.GapContent;
import javax.swing.text.PlainDocument;

/**
 *
 * @author msto
 */
public class StringStream extends JFrame implements PacketReceiver.StringPacketClient {

    private List<String> queue;
    private volatile boolean ignore;
    private volatile int maxlen;
    private volatile int last_index;

    private volatile boolean scheduled_reload;

    /**
     * Creates new form StringStream
     */
    public StringStream() {
        initComponents();

        queue = null;
        jToggleButton1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ignore = jToggleButton1.isSelected();
            }
        });

        Graph.configJSpinner(spinMaxLines, new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                maxlen = (Integer) spinMaxLines.getValue();
                scheduleReload();
            }
        });

        jTextArea1.setDocument(new PlainDocument(new GapContent()));

        ignore = false;
        maxlen = (Integer) spinMaxLines.getValue();
        last_index = 0;

        scheduled_reload = false;
    }

    private void scheduleReload() {
        if (!scheduled_reload) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    reload();
                }
            });
        }
    }

    private void reload() {
        scheduled_reload = false;

        Document g = jTextArea1.getDocument();

        // add on the latest stuff to the bottom
        int l = queue.size();
        while (last_index < l) {
            jTextArea1.append(queue.get(last_index) + "\n");
            last_index++;
        }

        // remove any extra length from the top
        int r = jTextArea1.getLineCount() - 1;
        while (r > maxlen) {
            String s = queue.get(queue.size() - r);
            try {
                g.remove(0, s.length() + 1);
            } catch (BadLocationException ex) {
                ex.printStackTrace(System.err);
                System.exit(1);
                break;
            }
            r--;
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        spinMaxLines = new javax.swing.JSpinner();
        jToggleButton1 = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setLayout(new java.awt.BorderLayout());

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.X_AXIS));

        jLabel1.setText("Max Lines");
        jPanel2.add(jLabel1);

        spinMaxLines.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(500), Integer.valueOf(500), null, Integer.valueOf(500)));
        jPanel2.add(spinMaxLines);

        jToggleButton1.setText("Ignore");
        jPanel2.add(jToggleButton1);

        getContentPane().add(jPanel2);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JSpinner spinMaxLines;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setQueue(List<String> s) {
        queue = s;
    }

    @Override
    public void newPackets(int k) {
        if (ignore) {
            return;
        }
        scheduleReload();
    }
}
