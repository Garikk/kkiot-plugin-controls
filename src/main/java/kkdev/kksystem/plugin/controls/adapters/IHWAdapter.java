/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kkdev.kksystem.plugin.controls.adapters;

/**
 *
 * @author blinov_is
 */
public interface IHWAdapter {
    public void RegisterHIDControl(String DevicePath, String Source, String ControlID,IHWAdapterCallback Callback);
}