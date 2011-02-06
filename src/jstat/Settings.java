/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat;

import java.beans.PropertyVetoException;

/**
 *
 * @author dmn
 */
public class Settings extends dmnutils.Settings<jstat.Settings> {
    static Settings main;

    static {
        main = new Settings();
        main.load("main.xml");
    }

    public static Settings get() {
        return main;
    }
    private Boolean toolbarBorderPainted = false;

    /**
     * Get the value of toolbarBorderPainted
     *
     * @return the value of toolbarBorderPainted
     */
    public Boolean getToolbarBorderPainted() {
        return toolbarBorderPainted;
    }

    /**
     * Set the value of toolbarBorderPainted
     *
     * @param newtoolbarBorderPainted new value of toolbarBorderPainted
     */
    public void setToolbarBorderPainted(Boolean newtoolbarBorderPainted) {
        toolbarBorderPainted = newtoolbarBorderPainted;
    }
    private Boolean toolbarButtonBorderPainted = false;
    public static final String PROP_TOOLBARBUTTONBORDERPAINTED = "toolbarButtonBorderPainted";

    /**
     * Get the value of toolbarButtonBorderPainted
     *
     * @return the value of toolbarButtonBorderPainted
     */
    public Boolean getToolbarButtonBorderPainted() {
        return toolbarButtonBorderPainted;
    }

    /**
     * Set the value of toolbarButtonBorderPainted
     *
     * @param newtoolbarButtonBorderPainted new value of toolbarButtonBorderPainted
     */
    public void setToolbarButtonBorderPainted(Boolean newtoolbarButtonBorderPainted) throws PropertyVetoException {
        Boolean oldtoolbarButtonBorderPainted = toolbarButtonBorderPainted;
        vetoableChangeSupport.fireVetoableChange(PROP_TOOLBARBUTTONBORDERPAINTED, oldtoolbarButtonBorderPainted, newtoolbarButtonBorderPainted);
        this.toolbarButtonBorderPainted = newtoolbarButtonBorderPainted;
        propertyChangeSupport.firePropertyChange(PROP_TOOLBARBUTTONBORDERPAINTED, oldtoolbarButtonBorderPainted, newtoolbarButtonBorderPainted);
    }
    private Boolean maximizeNewInternalFrame = true;
    public static final String PROP_MAXIMIZENEWINTERNALFRAME = "maximizeNewInternalFrame";

    /**
     * Get the value of maximizeNewInternalFrame
     *
     * @return the value of maximizeNewInternalFrame
     */
    public Boolean getMaximizeNewInternalFrame() {
        return maximizeNewInternalFrame;
    }

    /**
     * Set the value of maximizeNewInternalFrame
     *
     * @param newmaximizeNewInternalFrame new value of maximizeNewInternalFrame
     * @throws java.beans.PropertyVetoException
     */
    public void setMaximizeNewInternalFrame(Boolean newmaximizeNewInternalFrame) throws PropertyVetoException {
        Boolean oldmaximizeNewInternalFrame = maximizeNewInternalFrame;
        vetoableChangeSupport.fireVetoableChange(PROP_MAXIMIZENEWINTERNALFRAME, oldmaximizeNewInternalFrame, newmaximizeNewInternalFrame);
        this.maximizeNewInternalFrame = newmaximizeNewInternalFrame;
        propertyChangeSupport.firePropertyChange(PROP_MAXIMIZENEWINTERNALFRAME, oldmaximizeNewInternalFrame, newmaximizeNewInternalFrame);
    }

    public static enum ToolbarStyle {
        ICONSONLY, TEXTONLY, BOTH, BOTH_TEXTRIGHT,
    }
    private ToolbarStyle toolbarStyle = ToolbarStyle.ICONSONLY;
    public static final String PROP_TOOLBARSTYLE = "toolbarStyle";

    /**
     * Get the value of toolbarStyle
     *
     * @return the value of toolbarStyle
     */
    public ToolbarStyle getToolbarStyle() {
        return this.toolbarStyle;
    }

    /**
     * Set the value of toolbarStyle
     *
     * @param newtoolbarStyle new value of toolbarStyle
     * @throws java.beans.PropertyVetoException
     */
    public void setToolbarStyle(ToolbarStyle newtoolbarStyle) throws java.beans.PropertyVetoException {
        ToolbarStyle oldtoolbarStyle = toolbarStyle;
        vetoableChangeSupport.fireVetoableChange(PROP_TOOLBARSTYLE, oldtoolbarStyle, newtoolbarStyle);
        this.toolbarStyle = newtoolbarStyle;
        propertyChangeSupport.firePropertyChange(PROP_TOOLBARSTYLE, oldtoolbarStyle, newtoolbarStyle);
    }
    private Boolean fancyBackground = true;
    public static final String PROP_FANCYBACKGROUND = "fancyBackground";

    /**
     * Get the value of fancyBackground
     *
     * @return the value of fancyBackground
     */
    public Boolean getFancyBackground() {
        return this.fancyBackground;
    }

    /**
     * Set the value of fancyBackground
     *
     * @param newfancyBackground new value of fancyBackground
     * @throws java.beans.PropertyVetoException
     */
    public void setFancyBackground(Boolean newfancyBackground) throws java.beans.PropertyVetoException {
        Boolean oldfancyBackground = fancyBackground;
        vetoableChangeSupport.fireVetoableChange(PROP_FANCYBACKGROUND, oldfancyBackground, newfancyBackground);
        this.fancyBackground = newfancyBackground;
        propertyChangeSupport.firePropertyChange(PROP_FANCYBACKGROUND, oldfancyBackground, newfancyBackground);
    }
    private Boolean pluginsEnabled = true;
    public static final String PROP_PLUGINSENABLED = "pluginsEnabled";

    /**
     * Get the value of pluginsEnabled
     *
     * @return the value of pluginsEnabled
     */
    public Boolean getPluginsEnabled() {
        return this.pluginsEnabled;
    }

    /**
     * Set the value of pluginsEnabled
     *
     * @param newpluginsEnabled new value of pluginsEnabled
     * @throws java.beans.PropertyVetoException
     */
    public void setPluginsEnabled(Boolean newpluginsEnabled) throws java.beans.PropertyVetoException {
        Boolean oldpluginsEnabled = pluginsEnabled;
        vetoableChangeSupport.fireVetoableChange(PROP_PLUGINSENABLED, oldpluginsEnabled, newpluginsEnabled);
        this.pluginsEnabled = newpluginsEnabled;
        propertyChangeSupport.firePropertyChange(PROP_PLUGINSENABLED, oldpluginsEnabled, newpluginsEnabled);
    }
    private Boolean captureOutput = true;
    public static final String PROP_CAPTUREOUTPUT = "captureOutput";

    /**
     * Get the value of captureOutput
     *
     * @return the value of captureOutput
     */
    public Boolean getCaptureOutput() {
        return this.captureOutput;
    }

    /**
     * Set the value of captureOutput
     *
     * @param newcaptureOutput new value of captureOutput
     * @throws java.beans.PropertyVetoException
     */
    public void setCaptureOutput(Boolean newcaptureOutput) throws java.beans.PropertyVetoException {
        Boolean oldcaptureOutput = captureOutput;
        vetoableChangeSupport.fireVetoableChange(PROP_CAPTUREOUTPUT, oldcaptureOutput, newcaptureOutput);
        this.captureOutput = newcaptureOutput;
        propertyChangeSupport.firePropertyChange(PROP_CAPTUREOUTPUT, oldcaptureOutput, newcaptureOutput);
    }

    public static enum LookAndFeel {
        OCEAN, NATIVE,
    }
    private LookAndFeel lookAndFeel = LookAndFeel.NATIVE;
    public static final String PROP_LOOKANDFEEL = "lookAndFeel";

    /**
     * Get the value of lookAndFeel
     *
     * @return the value of lookAndFeel
     */
    public LookAndFeel getLookAndFeel() {
        return this.lookAndFeel;
    }

    /**
     * Set the value of lookAndFeel
     *
     * @param newlookAndFeel new value of lookAndFeel
     * @throws java.beans.PropertyVetoException
     */
    public void setLookAndFeel(LookAndFeel newlookAndFeel) throws java.beans.PropertyVetoException {
        LookAndFeel oldlookAndFeel = lookAndFeel;
        vetoableChangeSupport.fireVetoableChange(PROP_LOOKANDFEEL, oldlookAndFeel, newlookAndFeel);
        this.lookAndFeel = newlookAndFeel;
        propertyChangeSupport.firePropertyChange(PROP_LOOKANDFEEL, oldlookAndFeel, newlookAndFeel);
    }
    private boolean preferOnlineHelp = false;
    public static final String PROP_PREFERONLINEHELP = "preferOnlineHelp";

    /**
     * Get the value of preferOnlineHelp
     *
     * @return the value of preferOnlineHelp
     */
    public boolean getPreferOnlineHelp() {
        return preferOnlineHelp;
    }

    /**
     * Set the value of preferOnlineHelp
     *
     * @param preferOnlineHelp new value of preferOnlineHelp
     */
    public void setPreferOnlineHelp(boolean preferOnlineHelp) {
        boolean oldPreferOnlineHelp = preferOnlineHelp;
        this.preferOnlineHelp = preferOnlineHelp;
        propertyChangeSupport.firePropertyChange(PROP_PREFERONLINEHELP, oldPreferOnlineHelp, preferOnlineHelp);
    }


}
