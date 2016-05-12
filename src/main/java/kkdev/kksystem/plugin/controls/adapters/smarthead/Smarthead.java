/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.controls.adapters.smarthead;

import kkdev.kksystem.plugin.controls.adapters.debug.*;
import java.util.HashMap;
import java.util.Map;
import kkdev.kksystem.base.classes.base.PinBaseDataTaggedObj;
import static kkdev.kksystem.base.classes.controls.PinControlData.*;
import kkdev.kksystem.base.classes.plugins.PluginMessage;
import kkdev.kksystem.plugin.controls.adapters.IHWAdapter;
import kkdev.kksystem.plugin.controls.adapters.IHWAdapterCallback;
import kkdev.kksystem.plugin.controls.configuration.Adapter;
import kkdev.kksystem.plugin.controls.configuration.Control;
import kkdev.kksystem.plugin.controls.configuration.PluginSettings;

/**
 *
 * @author blinov_is
 */
public class Smarthead implements IHWAdapter {

    final String SmartheadControlPFX = "$K_CTRL_";
    final String SmartheadControlEvt_Trig = "EV_FIRE_";
    IHWAdapterCallback CB;
    Map<String, Control> Controls;
    boolean Active = false;
        Adapter Configuration;

    public Smarthead(Adapter MyConf) {
        Controls = new HashMap<>();
        Configuration=MyConf;
    }

    @Override
    public void RegisterControl(Control Ctrl, IHWAdapterCallback Callback) {

        CB = Callback;
        Controls.put(Ctrl.ID, Ctrl);

    }

    @Override
    public void SetActive() {
        Active = true;

    }

    @Override
    public void SetInactive() {
        Active = false;
    }

    @Override
    public void ReceiveObjPin(PluginMessage PM) {
        PinBaseDataTaggedObj ObjDat;

        ObjDat = (PinBaseDataTaggedObj) PM.PinData;

        if (!ObjDat.Tag.equals(Configuration.UNILPort)) {
            return;
        }

        CheckControl((String) ObjDat.Value);

    }

    public void CheckControl(String SmartheadString) {
        if (!SmartheadString.startsWith(SmartheadControlPFX)) {
            return;
        }

        SmartheadString = SmartheadString.substring(SmartheadControlPFX.length());
        //
        if (SmartheadString.startsWith(SmartheadControlEvt_Trig)) {
            SmartheadString = SmartheadString.substring(SmartheadControlEvt_Trig.length());
            if (Controls.containsKey(SmartheadString)) {
                CB.Control_Triggered(Controls.get(SmartheadString));
            }
        }
    }

}