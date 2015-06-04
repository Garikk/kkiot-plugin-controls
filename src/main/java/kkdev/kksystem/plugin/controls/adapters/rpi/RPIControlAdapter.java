/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.controls.adapters.rpi;

import java.util.HashMap;
import kkdev.kksystem.plugin.controls.adapters.IHWAdapter;
import kkdev.kksystem.plugin.controls.adapters.IHWAdapterCallback;

/**
 *
 * @author blinov_is
 */
public class RPIControlAdapter implements IHWAdapter {

    HashMap<String,IHWAdapterCallback> Controls;
    
    public RPIControlAdapter()
            {
                Controls=new HashMap<>();
            }
    
    
    @Override
    public void RegisterHIDControl(String DevicePath, String Source, String ControlID, IHWAdapterCallback Callback) {
        
        Controls.put(ControlID, Callback);
        RegisterEvent(DevicePath,Source);
    }
    
    
    private void RegisterEvent(String DevPath, String Source)
    {
    }
}
