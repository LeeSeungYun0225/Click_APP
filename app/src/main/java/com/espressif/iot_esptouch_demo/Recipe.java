package com.espressif.iot_esptouch_demo;

public class Recipe {

    private boolean m_status;
    private int m_recipe_id;
    private String m_if_this,m_this_gadget,m_creator,m_ifthis_complex;
    private String m_hour,m_min,m_day;
    private String m_then_what;
    private int m_channel_temp;

    public boolean Recipe()
    {
        m_recipe_id = -1;
        m_status = false;
        m_if_this = "";
        m_then_what = "";
        m_this_gadget = "";
        m_creator = "";
        m_ifthis_complex= "";
        m_channel_temp = -5;
        m_hour = "";
        m_min = "";
        m_day ="";

        return true;
    }

    public int m_get_channel_temp(){return m_channel_temp;}
    public boolean m_get_status(){return m_status;}
    public int m_get_recipeID(){return m_recipe_id;}
    public String m_get_if_this(){return m_if_this;}
    public String m_get_then_what(){return m_then_what;}
    public String m_get_this_gadget(){return m_this_gadget;}
    public String m_get_creator(){return m_creator;}
    public String m_get_ifthis_complex(){return m_ifthis_complex;}
    public String m_get_hour(){return m_hour;}
    public String m_get_min(){return m_min;}
    public String m_get_day(){return m_day;}

    public boolean m_set_channel_temp(int input)
    {
        m_channel_temp = input;
        return true;
    }

    public boolean m_set_day(String input)
    {
        m_day = input;
        return true;
    }
    public boolean m_set_min(String input)
    {
        m_min = input;
        return true;
    }

    public boolean m_set_hour(String input)
    {
        m_hour = input;
        return true;
    }

    public boolean m_set_ifthis_complex(String input)
    {
        m_ifthis_complex=input;
        return true;
    }

    public boolean m_set_creator(String input)
    {
        m_creator = input;
        return true;
    }

    public boolean m_set_if_this(String input)
    {
        m_if_this = input;
        return true;
    }

    public boolean m_set_status(boolean input) {
        m_status = input;
        return true;
    }

    public boolean m_set_recipe_id(int input)
    {
        m_recipe_id = input;
        return true;
    }
    public boolean m_set_then_what(String input)
    {
        m_then_what = input;
        return true;
    }
    public boolean m_set_this_gadget(String input)
    {
        m_this_gadget= input;
        return true;
    }





}
