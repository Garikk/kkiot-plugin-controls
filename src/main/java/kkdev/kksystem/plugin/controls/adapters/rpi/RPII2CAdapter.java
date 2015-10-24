/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.controls.adapters.rpi;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import kkdev.kksystem.plugin.controls.adapters.IHWAdapter;
import kkdev.kksystem.plugin.controls.adapters.IHWAdapterCallback;
import kkdev.kksystem.plugin.controls.configuration.Adapter;
import kkdev.kksystem.plugin.controls.configuration.Control;

/**
 *
 * @author sayma_000
 */
public class RPII2CAdapter implements IHWAdapter  {

    HashMap<String, DevCtrl> Devices;
    I2CBus BusI2C;
    Adapter Configuration;

      public RPII2CAdapter(Adapter Conf) {
        Devices = new HashMap<>();
        Configuration=Conf;
        try {
            if (Configuration.BusID==1)
            {
                BusI2C= I2CFactory.getInstance(I2CBus.BUS_1);
            }
            else
            {
                BusI2C= I2CFactory.getInstance(I2CBus.BUS_0);
            }
        } catch (IOException ex) {
            Logger.getLogger(RPII2CAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @Override
    public void SetActive() {
       StartBusReading();
    }

    @Override
    public void SetInactive() {

    }

     class DevCtrl {
        String AdapterID;
        HashMap<String,Control> MappedControls; //SrcID,CtrlID
        I2CDevice HWDev;
        Thread Reader;
        IHWAdapterCallback Callback;
        public DevCtrl(String DevID,I2CDevice Dev)
        {
            AdapterID=DevID;
            HWDev=Dev;
            MappedControls=new HashMap<>();
        }
        
    }

    @Override
    public void RegisterControl(Control Ctrl, IHWAdapterCallback Callback) {

        RegisterEvent(Ctrl,Callback);
    }
    
    private void RegisterEvent(Control Ctrl,IHWAdapterCallback Callback) {
        if (!Devices.containsKey(Ctrl.AdapterID))
        {
            Devices.put(Ctrl.AdapterID, ConnectI2CDevice(Ctrl.AdapterID));
        }
        //
        Devices.get(Ctrl.AdapterID).MappedControls.put(Ctrl.AdapterSource,Ctrl);
        Devices.get(Ctrl.AdapterID).Callback=Callback;
        
    }

    private void FireEvent(String DevID,byte EventType, byte Val) {
        if (EventType==49) //49 = ASCII  -> 1
        {
           //System.out.println("BUTTON " + EventType + " " + Val);
           Devices.get(DevID).Callback.Control_Triggered(Devices.get(DevID).MappedControls.get(String.valueOf((char)Val)));
       }
    }
    
    private DevCtrl ConnectI2CDevice(String DeviceID)
    {
        try {
            return new DevCtrl(DeviceID,BusI2C.getDevice(Configuration.DeviceID));
        } catch (IOException ex) {
            Logger.getLogger(RPII2CAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private void StartBusReading()
    {
        for (DevCtrl Dev:Devices.values())
        {
            final I2CDevice DV=Dev.HWDev;
            final String DevID=Dev.AdapterID;
           
            Dev.Reader=new Thread(new Runnable() {
                @Override
                public void run() //Этот метод будет выполняться в побочном потоке
                {
                    Boolean Stop = false;
                    while (!Stop) {
                        byte[] Dat=new byte[2];
                        try {
                            //System.out.println(DV.read(Dat,0,2));
                            DV.read(Dat,0,2);
                            
                            if (Dat[1]!=-1)
                            {
                                FireEvent(DevID,Dat[0],Dat[1]);
                            }
                          //  }
                            
                        } catch (IOException ex) {
                            Logger.getLogger(RPII2CAdapter.class.getName()).log(Level.SEVERE, null, ex);
                        }
                
                    }
                }
            });
            
            Dev.Reader.start();
        }
    }
}