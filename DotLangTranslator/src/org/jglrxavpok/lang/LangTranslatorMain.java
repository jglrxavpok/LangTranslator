package org.jglrxavpok.lang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.gtranslate.Language;
import com.gtranslate.Translator;

public class LangTranslatorMain extends JFrame
{

    private static final long serialVersionUID = 3604553382448461002L;
    private JTextArea content;
    private File currentFile;
    private JSplitPane pane;
    private HashMap<String, String> languageMap;
    private HashMap<String, String> reversedMap;
    protected LangFile lang;
    
    public LangTranslatorMain()
    {
        setTitle("LangTranslator");
        languageMap = this.getLanguagesFromGoogleTranslate();
        reversedMap = new HashMap<String, String>();
        Iterator<String> it = languageMap.keySet().iterator();
        Iterator<String> it1 = languageMap.values().iterator();
        while(it.hasNext())
        {
            reversedMap.put(it1.next(), it.next());
        }
        build();
        setSize(800,600);        
        setLocationRelativeTo(null);
    }
    
    private void build()
    {
        pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JTree tree = buildTree(new LangFile("Please select a file"));
        pane.setLeftComponent(tree);
        tree.setEditable(false);
        tree.setEnabled(false);
        content = new JTextArea();
        content.setEditable(false);
        content.setEnabled(false);
        content.addKeyListener(new KeyListener()
        {

            @Override
            public void keyPressed(KeyEvent arg0)
            {
            }

            @Override
            public void keyReleased(KeyEvent arg0)
            {
            }

            @Override
            public void keyTyped(KeyEvent arg0)
            {
                
            }
            
        });
        pane.setRightComponent(new JScrollPane(content));
        
        JPanel translation = new JPanel(new FlowLayout());
        translation.add(new JLabel("Translate from: "));
        String[] values = reversedMap.keySet().toArray(new String[0]);
        Arrays.sort(values, new Comparator<String>()
        {

            @Override
            public int compare(String arg0, String arg1)
            {
                return arg0.compareTo(arg1);
            }
            
        });
        final JComboBox from = new JComboBox(values);
        translation.add(from);
        translation.add(new JLabel(" to: "));
        final JComboBox to = new JComboBox(values);
        translation.add(to);

        MenuBar bar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem("Open");
        openItem.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(LangTranslatorMain.this);
                File f = chooser.getSelectedFile();
                if(f != null)
                {
                    currentFile = f;
                    try
                    {
                        InputStream in = new BufferedInputStream(new FileInputStream(f));
                        content.setText(IO.readString(in,"UTF-8"));
                        in.close();
                        lang = new LangFile(currentFile.getName());
                        lang.load(f);
                        pane.setLeftComponent(new JScrollPane(buildTree(lang)));
                        content.setEditable(true);
                        content.setEnabled(true);
                        if(lang.getTranslations().length > 0)
                        {
                            try
                            {
                                String s = Translator.getInstance().detect(lang.getTranslations()[0].getTranslated().split(" ")[0]);
                                from.setSelectedItem(languageMap.get(s));
                            }
                            catch(Exception e)
                            {
                            }
                        }
                    }
                    catch(Exception e)
                    {
                        lang = null;
                        e.printStackTrace();
                    }
                }
            }
            
        });
        fileMenu.add(openItem);
        MenuItem saveItem = new MenuItem("Save");
        saveItem.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                JFileChooser chooser = new JFileChooser();
                chooser.showSaveDialog(LangTranslatorMain.this);
                File file = chooser.getSelectedFile();
                if(file != null)
                {
                    try
                    {
                        file.createNewFile();
                        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                        DataOutputStream dataOut = new DataOutputStream(out);
                        dataOut.writeBytes(content.getText());
                        dataOut.flush();
                        dataOut.close();
                        out.close();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            
        });
        fileMenu.add(saveItem);
        bar.add(fileMenu);
        this.setMenuBar(bar);
        
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        add(contentPane);
        contentPane.add(pane);

        final JProgressBar pbar = new JProgressBar();
        pbar.setMaximum(100);
        pbar.setMinimum(0);
        contentPane.add(pbar,"North");
        JButton translate = new JButton("Translate!");
        translate.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                if(lang != null)
                {
                    new Thread()
                    {
                        public void run()
                        {
                            pbar.setStringPainted(true);
                            pbar.setString("Translating...");
                            String lfrom = reversedMap.get(from.getSelectedItem());
                            String lto = reversedMap.get(to.getSelectedItem());
                            Translation[] trans = lang.getTranslations();
                            String[] lines = new String[trans.length];
                            for(int i = 0;i<trans.length;i++)
                            {
                                Translation t;
                                try
                                {
                                    t = new Translation(trans[i].getUnlocalizedName(), new String(Translator.getInstance().translate(trans[i].getTranslated(), lfrom, lto).getBytes(),"Cp1252"));
                                }
                                catch (UnsupportedEncodingException e)
                                {
                                    e.printStackTrace();
                                    continue;
                                }
                                lines[i] = t.getUnlocalizedName()+"="+t.getTranslated();
                                System.out.println("Translated "+(i+1)+" lines out of "+trans.length);
                                pbar.setValue((int)((float)i/(float)trans.length*100f));
                            }
                            
                            Arrays.sort(lines, new Comparator<String>()
                                    {

                                        @Override
                                        public int compare(String arg0, String arg1)
                                        {
                                            return arg0.compareTo(arg1);
                                        }
                                        
                                    });
                            String finalText = "";
                            for(int i = 0;i<lines.length;i++)
                            {
                                if(i != 0)
                                    finalText+="\n";
                                finalText+=lines[i];
                            }
                            content.setText(finalText);
                        }
                    }.start();
                }
            }
        });
        translation.add(translate);
        contentPane.add(translation,"South");
    }
    
    @SuppressWarnings("unchecked")
    private HashMap<String, String> getLanguagesFromGoogleTranslate()
    {
        Language l= Language.getInstance();
        try
        {
            Field f = Language.class.getDeclaredField("hashLanguage");
            f.setAccessible(true);
            return (HashMap<String, String>) f.get(l);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private JTree buildTree(final LangFile lang)
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(lang.getLanguage());
        JTree tree = new JTree(root);
        Translation[] translations = lang.getTranslations();
        Arrays.sort(translations, new Comparator<Translation>()
                {

                    @Override
                    public int compare(Translation arg0, Translation arg1)
                    {
                        return arg0.getUnlocalizedName().compareTo(arg1.getUnlocalizedName());
                    }
                    
                });
        for(int i = 0;i<translations.length;i++)
        {
            if(translations[i] == null)
                continue;
            DefaultMutableTreeNode node = new LangNode(translations[i].getUnlocalizedName());
            root.add(node);
        }
        tree.addTreeSelectionListener(new TreeSelectionListener()
        {

            @Override
            public void valueChanged(TreeSelectionEvent arg0)
            {
                Object source = arg0.getPath().getLastPathComponent();
                if(source instanceof LangNode)
                {
                    LangNode node = (LangNode)source;
                    String s = node.getUnlocalizedName();
                    content.setSelectionColor(Color.BLUE);
                    content.setSelectedTextColor(Color.black);
                    content.setSelectionStart(content.getText().indexOf(s));
                    content.setSelectionEnd(content.getText().indexOf(s)+s.length());
                    content.requestFocus();
                }
            }
            
        });
        return tree;
    }

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        new LangTranslatorMain().setVisible(true);
    }
}
