package org.jglrxavpok.lang;

import javax.swing.tree.DefaultMutableTreeNode;

public class LangNode extends DefaultMutableTreeNode
{

    private static final long serialVersionUID = 2027408000594777063L;
    private String un;

    public LangNode(String un)
    {
        super(un);
        this.un = un;
    }
    
    public String getUnlocalizedName()
    {
        return un;
    }
}
