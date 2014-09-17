/*
 MyOpenLab by Carmelo Salafia www.myopenlab.de
 Copyright (C) 2004  Carmelo Salafia cswi@gmx.de

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package VisualLogic;

import VisualLogic.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.*;

class MyButton extends JButton {

    public String filePath;
    public String caption;
}

class CommandAction extends AbstractAction {

    String[] params;
    ElementPaletteIF elementPalette;

    public CommandAction(String[] params, ElementPaletteIF elementPalette) {
        super("");
        this.params = params;
        this.elementPalette = elementPalette;
    }

    public void actionPerformed(ActionEvent e) {
        elementPalette.onButtonClicken(params);
    }
}

public class ElementPalette extends javax.swing.JPanel {

    public String thePath = "";
    private boolean areVMsEditable = false;
    private String elementPath;
    public FrameMain frameCircuit = null;
    private ElementPaletteIF elementPalette;
    private String initVerzeichnis;
    public String activeElement;
    public String aktuellesVerzeichniss;
    public VMObject vmObject = null;
    private ElementPalette frm = this;
    private MyButton aktiveButton = null;
    public static int MODE_NONE = 0;
    public static int MODE_COPY = 1;
    public static int MODE_CUT = 2;
    public int modus = MODE_NONE;
    private String aktuellesVerz = "";
    private String toCopyPath = "";
    private boolean modusCut = false;
    private boolean gruppenAuswahlMode = false;
    public String rootPath;

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (int i = 0; i < jPanelButtons.getComponentCount(); i++) {
            jPanelButtons.getComponent(i).setEnabled(enabled);
        }
    }

    public ElementPalette() {
        initComponents();
    }

    public void init(ElementPaletteIF elementPalette, String elementPath, String rootPath) {
        this.elementPalette = elementPalette;
        this.elementPath = elementPath;
        this.aktuellesVerzeichniss = rootPath;
        this.rootPath = rootPath;
        loadFolder(aktuellesVerzeichniss);
    }

    public void setGruppenAuswahlMode(boolean value) {
        gruppenAuswahlMode = value;
    }

    public String getEndung(String fileName) {
        char ch;

        for (int i = 0; i < fileName.length(); i++) {
            ch = fileName.charAt(i);

            if (ch == '.') {
                return fileName.substring(i + 1);
            }
        }
        return null;
    }

    public Vector getSortDefinitionFile(File file) {
        Vector list = new Vector();
        try {
            BufferedReader input = new BufferedReader(new FileReader(file.getAbsolutePath() + "/" + "sort.def"));
            String inputString;
            while ((inputString = input.readLine()) != null) {
                list.add(inputString);
            }
            input.close();
        } catch (Exception ex) {
            //System.out.println("FEHLER : "+ex.getMessage());
        }
        return list;
    }

    public BufferedImage loadTransparentImage(String fileName) {
        Image image = Toolkit.getDefaultToolkit().getImage(fileName);
        image = Transparency.makeColorTransparent(image, new Color(0).white);

        MediaTracker mc = new MediaTracker(this);
        mc.addImage(image, 0);

        try {
            mc.waitForID(0);
        } catch (InterruptedException ex) {
        }

        BufferedImage bufferedimage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);

        Graphics g = bufferedimage.createGraphics();
        int x = (32 / 2) - (image.getWidth(this) / 2);
        int y = (32 / 2) - (image.getHeight(this) / 2);
        g.drawImage(image, x, y, this);
        return bufferedimage;
    }

    private int SucheStringInFiles_ResultIDX(String str, File[] files) {
        for (int i = 0; i < files.length; i++) {
            String val = files[i].getName();
            if (str.equalsIgnoreCase(val)) {
                return i;
            }
        }
        return -1;
    }

    private MyButton createBackButton() {
        MyButton btn = new MyButton();
        btn.setPreferredSize(new Dimension(38, 38));
        btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Bilder/back16.gif")));


        btn.setEnabled(true);
        if (aktuellesVerzeichniss.equalsIgnoreCase(rootPath)) {
            btn.setEnabled(false);
        }


        btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    if (!aktuellesVerzeichniss.equalsIgnoreCase(rootPath)) {
                        String str = aktuellesVerzeichniss.substring(0, aktuellesVerzeichniss.lastIndexOf("/"));
                        loadFolder(str);
                    }
                } catch (Exception ex) {
                    Tools.showMessage(ex.toString());
                }

            }
        });


        return btn;
    }

    private void reihenfolgeSortieren(File f, File files[]) {
        Vector lstReihenfolge = getSortDefinitionFile(f);

        // Sortiere die Files nach der lstReihenfolge!
        for (int i = 0; i < lstReihenfolge.size(); i++) {
            String str = (String) lstReihenfolge.get(i);
            int oldidx = SucheStringInFiles_ResultIDX(str, files);

            if (oldidx > -1) {
                File temp = files[oldidx];
                files[oldidx] = files[i];
                files[i] = temp;
            }
        }
    }

    public void reorderButtons() {
        Component c;
        int d = 0;
        if (jToggleButton1.isSelected()) {
            d = 100;
        }

        int w = 38 + d;
        int h = 38;
        int x = 0;
        int y = 0;
        for (int i = 0; i < jPanelButtons.getComponentCount(); i++) {
            c = jPanelButtons.getComponent(i);

            //((JButton)c).setBorderPainted(false);
            if (x + w > jPanelButtons.getWidth()) {
                x = 0;
                y += h;
            }
            c.setLocation(x, y);
            c.setSize(w, h);
            x += w;
        }

        jPanelButtons.setPreferredSize(new Dimension(w, y + h));
    }

    private void addButton(JButton button) {
        jPanelButtons.add(button);
        if (jToggleButton1.isSelected()) {
            button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        } else {
            button.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        }


    }

    public void loadFolder(String path) {
        jPanelButtons.removeAll();
        jPanelButtons.repaint();

        thePath = path;

        File f;

        String str = Tools.mapFile(elementPath + path);
        f = new File(str);


        aktuellesVerzeichniss = path;

        MyButton backbtn = createBackButton();
        addButton(backbtn);
        //jPanelButtons.add(backbtn);

        if (f.exists()) {
            File files[] = f.listFiles();
            aktuellesVerzeichniss = path;
            jLabel1.setText(path);
            jLabel1.setToolTipText(path);

            reihenfolgeSortieren(f, files);

            DFProperties thisDirProps = Tools.getProertiesFromDefinitionFile(f);

            /*if (thisDirProps.redirect.trim().length()>0)
             {              
             f = new File(thisDirProps.redirect.trim());              
             }*/

            if (thisDirProps.vm_dir_editable.length() > 0) {
                areVMsEditable = Boolean.valueOf(thisDirProps.vm_dir_editable);
            } else {
                areVMsEditable = false;
            }

            for (int i = 0; i < files.length; i++) {
                File file = files[i];

                if (file.isDirectory()) {
                    // Oeffne die Datei und interpretiere diese
                    DFProperties props = Tools.getProertiesFromDefinitionFile(file);

                    if (!gruppenAuswahlMode && !props.isDirectory && (props.classcircuit.length() > 0 || props.classfront.length() > 0 || props.vm.length() > 0 || props.loader.length() > 0)) {
                        MyButton btn = new MyButton();
                        btn.setPreferredSize(new Dimension(38, 38));

                        // Lade das Icons
                        String imagePath = file.getAbsolutePath() + "/" + props.iconFilename;
                        BufferedImage image = loadTransparentImage(imagePath);

                        String[] params = new String[6];
                        Action actionCmd = new CommandAction(params, elementPalette);

                        if (props.elementImage.length() == 0) {
                            props.elementImage = props.iconFilename;
                        }
                        if (props.loader.length() > 0) {
                            String filename = file.getName();

                            params[0] = props.loader;
                            params[1] = props.classcircuit;
                            params[2] = props.classfront;
                            params[3] = props.elementImage;
                            params[4] = path + "/" + filename;
                            params[5] = "LOADER";

                        } else if (props.vm.length() > 0) {
                            String filename = file.getName();

                            params[0] = path + "/" + filename;
                            params[1] = props.vm;
                            params[2] = props.captionInternationalized;
                            params[3] = params[0] + "/" + props.elementImage;
                            params[4] = props.classfront;
                            params[5] = "VM";
                        } else {
                            String filename = file.getName();

                            params[0] = path + "/" + filename;
                            params[1] = props.classcircuit;
                            params[2] = props.classfront;
                            params[3] = "";
                            params[4] = "";
                            params[5] = "NORMAL";
                        }

                        btn.setAction(actionCmd);
                        Image img = createImage(image.getSource());
                        // setzen des Icons zum Button
                        ImageIcon icon = new ImageIcon(img);
                        btn.setIcon(icon);
                        btn.setToolTipText(props.captionInternationalized);

                        btn.filePath = file.getAbsolutePath();
                        btn.caption = props.captionInternationalized;

                        if (jToggleButton1.isSelected()) {
                            btn.setText(btn.caption);
                        }

                        addButton(btn);
                        //jPanelButtons.add(btn);

                        btn.addMouseListener(new java.awt.event.MouseAdapter() {
                            public void mousePressed(java.awt.event.MouseEvent e) {
                                if (e.getButton() == e.BUTTON3) {
                                    MyButton button = (MyButton) e.getSource();
                                    aktiveButton = button;
                                    if (areVMsEditable) {
                                        jPopupMenu1.show(button, e.getX(), getY() + e.getY());
                                    }
                                }
                            }
                        });
                        btn.filePath = file.getAbsolutePath();


                    }

                    if (props.isDirectory) {
                        // is Directory=true
                        MyButton btn = new MyButton();
                        btn.setPreferredSize(new Dimension(38, 38));

                        // Laden des Icons
                        String imagePath = file.getAbsolutePath() + "/" + props.iconFilename;
                        BufferedImage image = loadTransparentImage(imagePath);

                        // Lade den Ordner
                        BufferedImage folder = loadTransparentImage(elementPath + "/" + "arrow.png");

                        // Folder und Icon verkn�pfen!
                        //folder.getGraphics().drawImage(image,4,7,null);
                        image.getGraphics().drawImage(folder, 0, 0, null);

                        // setzen des Icons zum Button
                        Image img = createImage(image.getSource());
                        //Image img = createImage(image.getSource());
                        ImageIcon icon = new ImageIcon(img);
                        btn.setIcon(icon);

                        btn.setToolTipText(props.captionInternationalized);
                        btn.setActionCommand(file.getName());
                        //jPanelButtons.add(btn);
                        if (jToggleButton1.isSelected()) {
                            btn.setText(props.captionInternationalized);
                        }
                        addButton(btn);
                        btn.filePath = file.getAbsolutePath();
                        btn.caption = props.captionInternationalized;
                        btn.addMouseListener(new java.awt.event.MouseAdapter() {
                            public void mousePressed(java.awt.event.MouseEvent e) {
                                if (e.getButton() == e.BUTTON3) {
                                    MyButton button = (MyButton) e.getSource();
                                    aktiveButton = button;

                                    if (areVMsEditable) {
                                        jPopupMenu3.show(button, e.getX(), getY() + e.getY());
                                    }


                                }
                            }
                        });

                        btn.addActionListener(new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                String cmd = evt.getActionCommand();

                                MyButton button = (MyButton) evt.getSource();
                                loadFolder(thePath + "/" + cmd);
                            }
                        });

                    }
                }
               /* synchronized (getTreeLock()) {
                    validateTree();
                }*/


            }
        } else {
            //Tools.showMessage("Elements Directory not found!");
        }

        reorderButtons();
        //jPanelButtons.setPreferredSize(new Dimension(0,600));
        /*if (jPanelButtons.getComponentCount()>0)
         {
         int index=jPanelButtons.getComponentCount()-1;
         System.out.println("index="+index);

         Component c = jPanelButtons.getComponent(index);
         Point p=c.getLocation();
         System.out.println("y="+p.y);
         jPanelButtons.setPreferredSize(new Dimension(jPanelButtons.getWidth(),p.y));
         }*/
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jmiEditVMDefinition = new javax.swing.JMenuItem();
        jmiEditVM = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jmiCut = new javax.swing.JMenuItem();
        jmiCopy = new javax.swing.JMenuItem();
        jmiDelete = new javax.swing.JMenuItem();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        jmiNewDir = new javax.swing.JMenuItem();
        jmiPaste = new javax.swing.JMenuItem();
        jPopupMenu3 = new javax.swing.JPopupMenu();
        jmiEditDir = new javax.swing.JMenuItem();
        jmiDeleteDir = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelButtons = new javax.swing.JPanel();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("VisualLogic/ElementPalette"); // NOI18N
        jmiEditVMDefinition.setText(bundle.getString("Menu_Edit")); // NOI18N
        jmiEditVMDefinition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiEditVMDefinitionActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jmiEditVMDefinition);

        jmiEditVM.setText(bundle.getString("EditVM")); // NOI18N
        jmiEditVM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiEditVMActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jmiEditVM);
        jPopupMenu1.add(jSeparator1);

        jmiCut.setText(bundle.getString("Menu_Cut")); // NOI18N
        jmiCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiCutActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jmiCut);

        jmiCopy.setText(bundle.getString("Menu_Copy")); // NOI18N
        jmiCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiCopyActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jmiCopy);

        jmiDelete.setText(bundle.getString("Menu_delete")); // NOI18N
        jmiDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiDeleteActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jmiDelete);

        jmiNewDir.setText(bundle.getString("New_Directory")); // NOI18N
        jmiNewDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiNewDirActionPerformed(evt);
            }
        });
        jPopupMenu2.add(jmiNewDir);

        jmiPaste.setText(bundle.getString("Menu_paste")); // NOI18N
        jmiPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiPasteActionPerformed(evt);
            }
        });
        jPopupMenu2.add(jmiPaste);

        jmiEditDir.setText(bundle.getString("edit_dir")); // NOI18N
        jmiEditDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiEditDirActionPerformed(evt);
            }
        });
        jPopupMenu3.add(jmiEditDir);

        jmiDeleteDir.setText(bundle.getString("Menu_delete")); // NOI18N
        jmiDeleteDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiDeleteDirActionPerformed(evt);
            }
        });
        jPopupMenu3.add(jmiDeleteDir);

        setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(200, 215));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setPreferredSize(new java.awt.Dimension(100, 25));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("jLabel1");
        jLabel1.setPreferredSize(new java.awt.Dimension(34, 15));

        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Bilder/16x16/Text.png"))); // NOI18N
        jToggleButton1.setToolTipText(bundle.getString("Show_Item_Names")); // NOI18N
        jToggleButton1.setPreferredSize(new java.awt.Dimension(25, 23));
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });
        jToggleButton1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jToggleButton1StateChanged(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Bilder/view-refresh.png"))); // NOI18N
        jButton1.setToolTipText("Reload");
        jButton1.setPreferredSize(new java.awt.Dimension(25, 23));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jToggleButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(14, 14, 14)
                .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
                    .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jToggleButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(29, 29, 29))
        );

        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

        jScrollPane1.setBorder(null);

        jPanelButtons.setMinimumSize(new java.awt.Dimension(9, 39));
        jPanelButtons.setPreferredSize(new java.awt.Dimension(200, 100));
        jPanelButtons.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanelButtonsMousePressed(evt);
            }
        });
        jPanelButtons.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jPanelButtonsComponentResized(evt);
            }
        });
        jPanelButtons.setLayout(null);

        jButton7.setText("XXX");
        jButton7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton7MousePressed(evt);
            }
        });
        jPanelButtons.add(jButton7);
        jButton7.setBounds(2, 2, 53, 23);

        jButton8.setText("XXX");
        jButton8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton8MousePressed(evt);
            }
        });
        jPanelButtons.add(jButton8);
        jButton8.setBounds(57, 2, 53, 23);

        jButton10.setText("XXX");
        jButton10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton10MousePressed(evt);
            }
        });
        jPanelButtons.add(jButton10);
        jButton10.setBounds(112, 2, 53, 23);

        jButton11.setText("XXX");
        jButton11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton11MousePressed(evt);
            }
        });
        jPanelButtons.add(jButton11);
        jButton11.setBounds(2, 27, 53, 23);

        jButton12.setText("XXX");
        jButton12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton12MousePressed(evt);
            }
        });
        jPanelButtons.add(jButton12);
        jButton12.setBounds(57, 27, 53, 23);

        jButton13.setText("XXX");
        jButton13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton13MousePressed(evt);
            }
        });
        jPanelButtons.add(jButton13);
        jButton13.setBounds(112, 27, 53, 23);

        jScrollPane1.setViewportView(jPanelButtons);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jToggleButton1ActionPerformed
    {//GEN-HEADEREND:event_jToggleButton1ActionPerformed
        loadFolder(aktuellesVerzeichniss);
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton1ActionPerformed
    {//GEN-HEADEREND:event_jButton1ActionPerformed
        loadFolder(aktuellesVerzeichniss);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jToggleButton1StateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_jToggleButton1StateChanged
    {//GEN-HEADEREND:event_jToggleButton1StateChanged
    }//GEN-LAST:event_jToggleButton1StateChanged

    private void jPanelButtonsComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanelButtonsComponentResized
        reorderButtons();
    }//GEN-LAST:event_jPanelButtonsComponentResized

    private void jButton13MousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jButton13MousePressed
    {//GEN-HEADEREND:event_jButton13MousePressed
// TODO add your handling code here:
    }//GEN-LAST:event_jButton13MousePressed

    private void jButton12MousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jButton12MousePressed
    {//GEN-HEADEREND:event_jButton12MousePressed
// TODO add your handling code here:
    }//GEN-LAST:event_jButton12MousePressed

    private void jButton11MousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jButton11MousePressed
    {//GEN-HEADEREND:event_jButton11MousePressed
// TODO add your handling code here:
    }//GEN-LAST:event_jButton11MousePressed

    private void jButton10MousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jButton10MousePressed
    {//GEN-HEADEREND:event_jButton10MousePressed
// TODO add your handling code here:
    }//GEN-LAST:event_jButton10MousePressed

    private void jButton8MousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jButton8MousePressed
    {//GEN-HEADEREND:event_jButton8MousePressed
// TODO add your handling code here:
    }//GEN-LAST:event_jButton8MousePressed

    private void jButton7MousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jButton7MousePressed
    {//GEN-HEADEREND:event_jButton7MousePressed
// TODO add your handling code here:
    }//GEN-LAST:event_jButton7MousePressed

    private void jmiEditVMActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jmiEditVMActionPerformed
    {//GEN-HEADEREND:event_jmiEditVMActionPerformed

        if (aktiveButton != null) {
            String str = Tools.mapFile(aktiveButton.filePath);
            frameCircuit.openElement(str);

            //frameCircuit.getAktuelleBasis().openVLogicFileAsFrontPanel(aktiveButton.filePath+"/"+definition_def.vm);
        }

    }//GEN-LAST:event_jmiEditVMActionPerformed

    private void jmiEditDirActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jmiEditDirActionPerformed
    {//GEN-HEADEREND:event_jmiEditDirActionPerformed
        DialogSaveAsModul frm = new DialogSaveAsModul(frameCircuit, frameCircuit, true);

        String oldPath = aktiveButton.filePath;
        frm.executeEditDirectory(oldPath);
        frm.setVisible(true);

        if (frm.result) {
            String newPath = elementPath + aktuellesVerzeichniss + "/" + frm.xname;

            //File file=new File(newPath);

            //if (new File(oldPath).getAbsolutePath().equalsIgnoreCase(new File(newPath).getAbsolutePath()))
            {
                String ext = Tools.getExtension(new File(frm.xicon));

                String newIcon = oldPath + "/icon." + ext;
                File f1 = new File(frm.xicon);
                File f2 = new File(newIcon);
                String ff1 = f1.getAbsolutePath();
                String ff2 = f2.getAbsolutePath();
                if (!ff1.equalsIgnoreCase(ff2)) {
                    //new File(oldXIcon).delete();
                    try {
                        Tools.copyFile(new File(frm.xicon), new File(newIcon));
                    } catch (Exception ex) {
                        //Tools.showMessage("Error: copying icon file!");
                    }
                }

                DFProperties props = new DFProperties();
                props.isDirectory = true;
                props.captionDE = frm.caption_DE;
                props.captionEN = frm.caption_EN;
                props.captionES = frm.caption_ES;
                props.iconFilename = "icon." + ext;
                props.vm_dir_editable = "TRUE";
                Tools.saveDefinitionFile(new File(newPath), props);
                loadFolder(aktuellesVerzeichniss);
            }
        }

    }//GEN-LAST:event_jmiEditDirActionPerformed

    private void jmiNewDirActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jmiNewDirActionPerformed
    {//GEN-HEADEREND:event_jmiNewDirActionPerformed

        DialogSaveAsModul frm = new DialogSaveAsModul(frameCircuit, frameCircuit, true);

        frm.executeNewDirectory();
        frm.setVisible(true);

        if (frm.result) {
            String newPath = elementPath + aktuellesVerzeichniss + "/" + frm.xname;
            File file = new File(newPath);

            if (!file.exists()) {
                boolean success = file.mkdir();
                if (!success) {
                    Tools.showMessage("Error: Directory\"" + file.getPath() + "\" not created!");
                    return;
                }


                String ext = Tools.getExtension(new File(frm.xicon));

                String newIcon = file.getPath() + "/icon." + ext;
                try {
                    Tools.copyFile(new File(frm.xicon), new File(newIcon));
                } catch (Exception ex) {
                }


                DFProperties props = new DFProperties();
                props.isDirectory = true;
                props.captionDE = frm.caption_DE;
                props.captionEN = frm.caption_EN;
                props.captionES = frm.caption_ES;
                props.iconFilename = "icon." + ext;
                props.vm_dir_editable = "TRUE";
                Tools.saveDefinitionFile(file, props);
            } else {
                Tools.showMessage("Directory already exist : \"" + newPath + "\"");
            }
            loadFolder(aktuellesVerzeichniss);
        }
    }//GEN-LAST:event_jmiNewDirActionPerformed

    private void jmiDeleteDirActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jmiDeleteDirActionPerformed
    {//GEN-HEADEREND:event_jmiDeleteDirActionPerformed
        int result = JOptionPane.showConfirmDialog((Component) null, java.util.ResourceBundle.getBundle("VisualLogic/ElementPalette").getString("really_delete") + " : " + aktiveButton.caption, java.util.ResourceBundle.getBundle("VisualLogic/ElementPalette").getString("Attention"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            String path = aktiveButton.filePath;
            Tools.deleteDirectory(new File(path));
            loadFolder(aktuellesVerzeichniss);
        }

    }//GEN-LAST:event_jmiDeleteDirActionPerformed

    private void jmiEditVMDefinitionActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jmiEditVMDefinitionActionPerformed
    {//GEN-HEADEREND:event_jmiEditVMDefinitionActionPerformed

        DFProperties props = Tools.getProertiesFromDefinitionFile(new File(aktiveButton.filePath));

        if (props.vm.endsWith("vlogic")) {
            if (aktiveButton != null && frameCircuit != null) {
                frameCircuit.editModule(aktiveButton.filePath);
                loadFolder(aktuellesVerzeichniss);
            }
        } else {
            codeeditor.frmDefinitonDefEditor frm = new codeeditor.frmDefinitonDefEditor(frameCircuit, true);
            frm.execute(aktiveButton.filePath);
            loadFolder(aktuellesVerzeichniss);
        }




    }//GEN-LAST:event_jmiEditVMDefinitionActionPerformed

    private void jmiCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiCutActionPerformed
        modus = MODE_COPY;
        modusCut = true;
        toCopyPath = aktiveButton.filePath;
    }//GEN-LAST:event_jmiCutActionPerformed

    private void jmiDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiDeleteActionPerformed

        int result = JOptionPane.showConfirmDialog((Component) null, java.util.ResourceBundle.getBundle("VisualLogic/ElementPalette").getString("really_delete") + " : " + aktiveButton.caption, java.util.ResourceBundle.getBundle("VisualLogic/ElementPalette").getString("Attention"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            String path = aktiveButton.filePath;
            Tools.deleteDirectory(new File(path));
            loadFolder(aktuellesVerzeichniss);
        }


    }//GEN-LAST:event_jmiDeleteActionPerformed

    private void jPanelButtonsMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jPanelButtonsMousePressed
    {//GEN-HEADEREND:event_jPanelButtonsMousePressed
        if (evt.getButton() == 3) {
            if (modus == MODE_COPY) {
                jmiPaste.setEnabled(true);
            } else {
                jmiPaste.setEnabled(false);
            }
            jPopupMenu2.show(jPanelButtons, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jPanelButtonsMousePressed

    private void jmiPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiPasteActionPerformed

        if (modus == MODE_COPY) {
            String str = toCopyPath.substring(toCopyPath.lastIndexOf("\\"), toCopyPath.length());
            String path = elementPath + aktuellesVerzeichniss + str;
            if (new File(path).exists() == false) {
                try {
                    Tools.copy(new File(toCopyPath), new File(path));
                    loadFolder(aktuellesVerzeichniss);
                } catch (IOException ex) {
                    Tools.showMessage(ex.toString());
                }
            } else {
                Tools.showMessage(java.util.ResourceBundle.getBundle("VisualLogic/ElementPalette").getString("Element ist bereits vorhanden"));
            }

            if (modusCut) {
                Tools.deleteDirectory(new File(toCopyPath));
            }
            modusCut = false;
            modus = MODE_NONE;
        }


    }//GEN-LAST:event_jmiPasteActionPerformed

    private void jmiCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiCopyActionPerformed
        toCopyPath = aktiveButton.filePath;
        modus = MODE_COPY;
        modusCut = false;
    }//GEN-LAST:event_jmiCopyActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    public javax.swing.JPanel jPanelButtons;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JPopupMenu jPopupMenu3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JMenuItem jmiCopy;
    private javax.swing.JMenuItem jmiCut;
    private javax.swing.JMenuItem jmiDelete;
    private javax.swing.JMenuItem jmiDeleteDir;
    private javax.swing.JMenuItem jmiEditDir;
    private javax.swing.JMenuItem jmiEditVM;
    private javax.swing.JMenuItem jmiEditVMDefinition;
    private javax.swing.JMenuItem jmiNewDir;
    private javax.swing.JMenuItem jmiPaste;
    // End of variables declaration//GEN-END:variables
}
