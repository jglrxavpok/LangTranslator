package org.jglrxavpok.lang;

public class Translation
{

    private String translated;
    private String unlocalizedName;

    public Translation(String unlocalizedName, String translation)
    {
        this.unlocalizedName = unlocalizedName;
        this.translated = translation;
    }
    
    public String getUnlocalizedName()
    {
        return unlocalizedName;
    }
    
    public String getTranslated()
    {
        return translated;
    }
}
