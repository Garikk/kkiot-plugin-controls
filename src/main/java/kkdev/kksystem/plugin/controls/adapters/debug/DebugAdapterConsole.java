/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.controls.adapters.debug;

import java.util.HashMap;
import java.util.Map;
import static kkdev.kksystem.base.classes.controls.PinControlData.*;
import kkdev.kksystem.plugin.controls.adapters.IHWAdapter;
import kkdev.kksystem.plugin.controls.adapters.IHWAdapterCallback;
import kkdev.kksystem.plugin.controls.configuration.Control;

/**
 *
 * @author blinov_is
 */
public class DebugAdapterConsole implements IHWAdapter {

    IHWAdapterCallback CB;
    Map<String, Control> Controls;
    boolean Active=false;
    
    public DebugAdapterConsole()
    {
        Controls=new HashMap<>();
    }
    
    
    @Override
    public void RegisterControl(Control Ctrl, IHWAdapterCallback Callback) {
       
        CB=Callback;
        
        Controls.put(Ctrl.ID, Ctrl);

        Thread tmr;
        tmr = new Thread(new Runnable() {
            public void run() //Этот метод будет выполняться в побочном потоке
            {
                Boolean Stop = false;
                String Check;

                while (!Stop) {
                    if (System.console()==null){
                        Stop=true;
                        continue;
                    }
                    
                    Check = System.console().readLine();
                    System.out.println(Check.length());
                    
                    switch (Check) {
                         case "1":
                            CB.Control_Triggered(Controls.get(DEF_BTN_UP));
                            break;
                        case "2":
                            CB.Control_Triggered(Controls.get(DEF_BTN_DOWN));
                            break;
                        case "3":
                            CB.Control_Triggered(Controls.get(DEF_BTN_ENTER));
                            break;
                        case "4":
                            CB.Control_Triggered(Controls.get(DEF_BTN_BACK));
                            break;
                        case "44":
                            CB.Control_LongPress(Controls.get(DEF_BTN_BACK),1);
                            break;
                    }
                }
            }
        });
        
        tmr.start();
    }

    @Override
    public void SetActive() {
        Active = false;
    }

    @Override
    public void SetInactive() {
        Active=false;
    }
    
    

}