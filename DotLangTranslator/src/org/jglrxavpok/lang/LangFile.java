package org.jglrxavpok.lang;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class LangFile
{

    private Translation[] translations;
    private String language;

    public LangFile(String language)
    {
        this.language = language;
        translations = new Translation[0];
    }

    public Translation[] getTranslations()
    {
        return translations;
    }
    
    public void load(File file)
    {
        Properties props = new Properties();
        try
        {
            InputStream in = new FileInputStream(file);
            props.load(in);
            Iterator<Object> it = props.keySet().iterator();
            Iterator<Object> it1 = props.values().iterator();
            ArrayList<Translation> trans = new ArrayList<Translation>();
            while(it.hasNext())
            {
                String str = (String) it.next();
                trans.add(new Translation(str, (String) it1.next()));
            }
            translations = trans.toArray(new Translation[0]);
            in.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getLanguage()
    {
        return language;
    }
}
