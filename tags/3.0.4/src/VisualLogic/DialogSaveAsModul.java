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

import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 *
 * @author  Carmelo
 */
public class DialogSaveAsModul extends javax.swing.JDialog implements ElementPaletteIF
{
    
    private ElementPalette elementPalette;
    private MyImage image=new MyImage();
    private String letztesVerzeichniss=".";
    private boolean editing=false;
    private FrameMain frameCircuit;
    private boolean isCircuitElement=true;
    private boolean modeEdit=false;
    
    
    /** Creates new form DialogSaveAsModul */
    public DialogSaveAsModul(java.awt.Frame parent,FrameMain frameCircuit, boolean modal)
    {
        super(parent, modal);
        initComponents();
        this.frameCircuit=frameCircuit;
        elementPalette = new ElementPalette();
        elementPalette.setGruppenAuswahlMode(true);
        
        //elementPalette.rootCircuitPath+="/2user-defined/";
        //elementPalette.rootFrontPath+="/2user-defined/";
        jPanel5.add(elementPalette,java.awt.BorderLayout.CENTER);
        jPanel2.add(image,java.awt.BorderLayout.CENTER);
        image.setBackground(Color.WHITE);
        
        java.awt.event.ActionListener actionListener = new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent actionEvent)
            {
                result=false;
                dispose();
            }
        };
        
        javax.swing.KeyStroke stroke = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0);
        rootPane.registerKeyboardAction(actionListener, stroke, javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width-getWidth())/2, (screenSize.height-getHeight())/2);        
        
    }
    
    public void savedocFile(JEditorPane pane,String filename)
    {
        try
        {
            FileWriter out = new FileWriter(new File(filename));
            pane.setContentType("text/html");
            pane.write(out);
            out.flush();
            out.close();
            
        }
        catch(IOException ioe)
        {
            VisualLogic.Tools.showMessage(ioe.toString());
        }
        
    }
    
    public void loadFile(JEditorPane pane,String filename)
    {
        
        try
        {
            pane.setPage(new URL("file:"+filename));
            pane.setContentType("text/html");
            pane.setCaretPosition(0);
            
        }
        catch(Exception e)
        {
            //VisualLogic.Tools.showMessage(e.toString());
        }
    }
    
    public void executeNewDirectory()
    {
        //jPanel1.setVisible(false);
        setTitle(java.util.ResourceBundle.getBundle("VisualLogic/ElementPalette").getString("New_Directory"));
        jPanel5.setVisible(false);
        jPanel9.setVisible(false);
        jTabbedPane2.setVisible(false);
        jCheckBox1.setVisible(false);
    }
    
    
    
    public void executeEditDirectory(String directory)
    {
        //jPanel1.setVisible(false);
        txtName.setEnabled(false);
        setTitle(java.util.ResourceBundle.getBundle("VisualLogic/ElementPalette").getString("edit_dir"));
        jPanel5.setVisible(false);
        jPanel9.setVisible(false);
        jTabbedPane2.setVisible(false);
        jCheckBox1.setVisible(false);
        
        
        DFProperties definition_def = Tools.getProertiesFromDefinitionFile(new File(directory));
        txtDE.setText(definition_def.captionDE);
        txtEN.setText(definition_def.captionEN);
        txtES.setText(definition_def.captionES);
        
        int index=directory.lastIndexOf("\\");
        
        String n = directory.substring(index+1,directory.length());
        
        //Basis basis=frameCircuit.basis;
        txtIcon.setText(directory+"/"+definition_def.iconFilename);
        
        openImage(txtIcon.getText());
        
        
        txtName.setText(n);
    }
    
    public void executeEdit(String baseDir)
    {                
        setTitle(java.util.ResourceBundle.getBundle("VisualLogic/ElementPalette").getString("Menu_Edit"));
        txtName.setEnabled(false);                
        
        modeEdit=true;
        execute();
        // 1. aus dem Verzeichniss die definition.def auslesen
        //    Icon und Hilfe auch!
        // 2. aus der definition.def die Daten anzeigen
        //    Icon und Hilfe auch!
        // 3. definition.def anhand der neuen Daten neu generieren
        //    Icon und Hilfe auch!
        
        
        DFProperties definition_def = Tools.getProertiesFromDefinitionFile(new File(baseDir));
        
        if (definition_def.vm.length()==0)
        {
            return;
        }
        
        txtDE.setText(definition_def.captionDE);
        txtEN.setText(definition_def.captionEN);
        txtES.setText(definition_def.captionES);
        
        String extension=Tools.getExtension(new File(definition_def.vm));
        
        editing=true;
        
        if (definition_def.vm.length()>0)
        {
          txtName.setText(definition_def.vm.substring(0,definition_def.vm.length()-extension.length()-1));
        }
        
        String oldXIcon=baseDir+"/"+definition_def.iconFilename;
        txtIcon.setText(oldXIcon);
        letztesVerzeichniss=baseDir;
        
        loadFile(editorDE,baseDir+"/doc.html");
        loadFile(editorEN,baseDir+"/doc_en.html");
        loadFile(editorES,baseDir+"/doc_es.html");
        
        
        openImage(txtIcon.getText());
        
        jPanel5.setVisible(false);
        setVisible(true);
        
        if (result)
        {
            savedocFile(editorDE,baseDir+"/doc.html");
            savedocFile(editorEN,baseDir+"/doc_en.html");
            savedocFile(editorES,baseDir+"/doc_es.html");
            
            String ext=Tools.getExtension(new File(xicon));
            
            DFProperties definition_def2 = new DFProperties();
            File f1=new File(oldXIcon);
            File f2=new File(xicon);
            String ff1=f1.getAbsolutePath();
            String ff2=f2.getAbsolutePath();
            if (!ff1.equalsIgnoreCase(ff2))
            {
                new File(oldXIcon).delete();
                
                try
                {
                    Tools.copyFile(new File(xicon), new File(baseDir+"/icon."+ext));
                }
                catch(Exception ex)
                {
                    Tools.showMessage("Error: copying icon file!");
                }
            }
            
            definition_def2.captionDE=caption_DE;
            definition_def2.captionEN=caption_EN;
            definition_def2.captionES=caption_ES;
            definition_def2.resizeSynchron=resizeProportional;
            definition_def2.classcircuit="";
            definition_def2.iconFilename="icon."+ext;
            definition_def2.classfront=definition_def.classfront;
            System.out.println("definition_def.classfront="+definition_def2.classfront);
            /*if (circuitElement==true) {
                definition_def2.classfront="";
            }else {
                definition_def2.classfront="TRUE";
            }*/
            
            definition_def2.vm=xname+".vlogic";
            
            Tools.saveDefinitionFile(new File(baseDir),definition_def2);
            //frameCircuit.elementPalette.loadFolder(frameCircuit.elementPalette.aktuellesVerzeichniss);
        }
    }
    
    
    public void executeNew()
    {        
        jButton4.setVisible(false);
        jCheckBox1.setSelected(true);
        editing=false;
        execute();
        setVisible(true);
        if (result)
        {
            // 1. Verzeichniss für das Element generieren (unter group!)
            // 2. Icon von der Quelle zum verzeichniss kopieren
            // 3. definition.def in das Verzeichniss generieren
            
            String baseDir=frameCircuit.getAktuelleBasis().getElementPath()+group+"/"+xname;
            baseDir=Tools.mapFile(baseDir);
            
            File file = new File(baseDir);
            
            if (!file.exists())
            {
                boolean success = file.mkdir();
                if (!success)
                {
                    Tools.showMessage("Error: Directory\""+file.getPath()+"\" not created!");
                }
            }
            
            String vmFile=baseDir;
            
            frameCircuit.getAktuelleBasis().saveToFile(vmFile+"/"+xname+".vlogic",false);
            
            savedocFile(editorDE,baseDir+"/doc.html");
            savedocFile(editorEN,baseDir+"/doc_en.html");
            savedocFile(editorES,baseDir+"/doc_es.html");
            
            String ext=Tools.getExtension(new File(xicon));
            
            try
            {
                Tools.copyFile(new File(xicon), new File(baseDir+"/icon."+ext));
            }
            catch(Exception ex)
            {
                Tools.showMessage("Error: copying icon file!");
            }
            
            DFProperties definition_def = new DFProperties();
            definition_def.iconFilename="icon."+ext;
            definition_def.captionDE=caption_DE;
            definition_def.captionEN=caption_EN;
            definition_def.captionES=caption_ES;
            definition_def.classcircuit="";
            definition_def.resizeSynchron=resizeProportional;
            
            
            if (circuitElement==true)
            {
                definition_def.classfront="";
            }
            else
            {
                definition_def.classfront="TRUE";
            }
            
            definition_def.vm=xname+".vlogic";
            
            Tools.saveDefinitionFile(new File(baseDir),definition_def);
        }
        
    }
    
    
    public void execute()
    {
        if (modeEdit==false)
        {
            Basis basis=frameCircuit.getAktuelleBasis();

            int c=basis.getFrontBasis().getElementCount();
            if (c==0)
            {
                elementPalette.aktuellesVerzeichniss="/CircuitElements/2user-defined/";
                isCircuitElement=true;
            }
            else
            {
                elementPalette.aktuellesVerzeichniss="/FrontElements/2user-defined/";
                isCircuitElement=false;
            }
            
            elementPalette.init(this,basis.getElementPath(),elementPalette.aktuellesVerzeichniss);
            txtIcon.setText(basis.getElementPath()+"/element.gif");
        }
        
        jCheckBox1.setSelected(resizeProportional);
                
        openImage(txtIcon.getText());
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        txtName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtDE = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtEN = new javax.swing.JTextField();
        txtES = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtIcon = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel9 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        editorDE = new javax.swing.JEditorPane();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        editorEN = new javax.swing.JEditorPane();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        editorES = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("VisualLogic/FrameCircuit"); // NOI18N
        setTitle(bundle.getString("Als_Modul_speichern")); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jButton1.setText("Cancel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("OK");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel5.setText("Name : ");

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Caption"));

        jLabel1.setText("German");

        txtDE.setPreferredSize(new java.awt.Dimension(100, 20));

        jLabel3.setText("English");

        jLabel4.setText("Spanisch");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jLabel3)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtES, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                    .add(txtEN, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                    .add(txtDE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(txtDE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(txtEN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(txtES, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Icon"));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel2.setText("Icon");

        txtIcon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtIconKeyPressed(evt);
            }
        });

        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("VisualLogic/frmOptions"); // NOI18N
        jButton3.setText(bundle1.getString("durchsuchen")); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("edit icon");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jButton4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 114, Short.MAX_VALUE)
                        .add(jButton3))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtIcon, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(txtIcon, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jButton3)
                            .add(jButton4)))
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel5.setLayout(new java.awt.BorderLayout());

        java.util.ResourceBundle bundle2 = java.util.ResourceBundle.getBundle("VisualLogic/DialogSaveAsModul"); // NOI18N
        jCheckBox1.setText(bundle2.getString("resize_proportional")); // NOI18N
        jCheckBox1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4Layout.createSequentialGroup()
                        .add(jLabel5)
                        .add(5, 5, 5)
                        .add(txtName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jCheckBox1))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(txtName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jCheckBox1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Settings", jPanel4);

        editorDE.setContentType("text/html");
        jScrollPane1.setViewportView(editorDE);

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("German", jPanel10);

        editorEN.setContentType("text/html");
        jScrollPane2.setViewportView(editorEN);

        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("English", jPanel11);

        editorES.setContentType("text/html");
        jScrollPane3.setViewportView(editorES);

        org.jdesktop.layout.GroupLayout jPanel12Layout = new org.jdesktop.layout.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Spanish", jPanel12);

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Dokumentation", jPanel9);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jButton2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void formWindowActivated(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowActivated
    {//GEN-HEADEREND:event_formWindowActivated
// TODO add your handling code here:
    }//GEN-LAST:event_formWindowActivated
    
    private void formWindowClosed(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosed
    {//GEN-HEADEREND:event_formWindowClosed
        
        
    }//GEN-LAST:event_formWindowClosed
    
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        String param=new File(txtIcon.getText()).getPath();
        
        Tools.openPaint(new File(param));
               
        
    }//GEN-LAST:event_jButton4ActionPerformed
    
    private void txtIconKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_txtIconKeyPressed
    {//GEN-HEADEREND:event_txtIconKeyPressed
        if (evt.getKeyCode()==KeyEvent.VK_ENTER)
        {
            openImage(txtIcon.getText());
        }
    }//GEN-LAST:event_txtIconKeyPressed
    
    private void openImage(String filename)
    {
        
        Image img = getToolkit().getImage(filename);
        image.setImage(img);
        
    }
    
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File(letztesVerzeichniss));
        
        ExtensionFileFilter filter = new ExtensionFileFilter(  );
        
        filter.addExtension("gif");
        filter.addExtension("png");
        filter.addExtension("jpg");
        
        filter.setDescription("gif, png, jpg");
        
        
        chooser.addChoosableFileFilter(filter);
        
        int value = chooser.showOpenDialog(this);
        
        if (value == JFileChooser.APPROVE_OPTION)
        {
            File file = chooser.getSelectedFile();
            
            letztesVerzeichniss=chooser.getCurrentDirectory().getPath();
            
            String fileName=file.getPath();
            txtIcon.setText(fileName);
            openImage(fileName);
        }
    }//GEN-LAST:event_jButton3ActionPerformed
    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        
        
        if (modeEdit)
        {
            circuitElement=isCircuitElement;
            xname=txtName.getText();
            resizeProportional=jCheckBox1.isSelected();

            if (editing)
            {
                if (!xname.equalsIgnoreCase("") )
                {
                    caption_DE=txtDE.getText();
                    caption_EN=txtEN.getText();
                    caption_ES=txtES.getText();

                    xicon=txtIcon.getText();

                    doc_DE=editorDE.getText();
                    doc_EN=editorEN.getText();
                    doc_ES=editorES.getText();

                    result=true;
                    dispose();
                }
                else
                {
                    Tools.showMessage("Name is invalid!");
                }

            }
        }else
        {
            circuitElement=isCircuitElement;
            xname=txtName.getText();
            resizeProportional=jCheckBox1.isSelected();
            group=elementPalette.aktuellesVerzeichniss;        
            String baseDir=frameCircuit.getAktuelleBasis().getElementPath()+group+xname;
            baseDir=Tools.mapFile(baseDir);

            if (editing)
            {
                if (!xname.equalsIgnoreCase("") )
                {
                    caption_DE=txtDE.getText();
                    caption_EN=txtEN.getText();
                    caption_ES=txtES.getText();

                    xicon=txtIcon.getText();

                    doc_DE=editorDE.getText();
                    doc_EN=editorEN.getText();
                    doc_ES=editorES.getText();

                    result=true;
                    dispose();
                }
                else
                {
                    Tools.showMessage("Name is invalid!");
                }

            }
            else
            {

                if (!xname.equalsIgnoreCase("") && new File(baseDir).exists()==false)
                {
                    caption_DE=txtDE.getText();
                    caption_EN=txtEN.getText();
                    caption_ES=txtES.getText();

                    xicon=txtIcon.getText();

                    doc_DE=editorDE.getText();
                    doc_EN=editorEN.getText();
                    doc_ES=editorES.getText();

                    result=true;
                    dispose();
                }
                else
                {
                    Tools.showMessage("Name is invalid!");
                }
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        result=false;
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed
    
    public void onButtonClicken(String[] params)
    {
    }
    
    
    public static String xicon="";
    public static String xname="";
    public static boolean resizeProportional=true;
    public static String caption_DE="";
    public static String caption_EN="";
    public static String caption_ES="";
    public static boolean circuitElement=false;
    
    public static String doc_DE="";
    public static String doc_EN="";
    public static String doc_ES="";
    public static String group="";
    
    public static boolean result=false;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JEditorPane editorDE;
    public static javax.swing.JEditorPane editorEN;
    public static javax.swing.JEditorPane editorES;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextField txtDE;
    private javax.swing.JTextField txtEN;
    private javax.swing.JTextField txtES;
    private javax.swing.JTextField txtIcon;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
    
}
