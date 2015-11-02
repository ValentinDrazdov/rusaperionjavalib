/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saperion.tools;

import org.zkoss.zul.*;
/**
 *
 * @author VDrazdov
 */
public class WebWorker {
    public static void SaveFile(String fileName, byte[] content)
    {
        Filedownload.save(content, "application/octet-stream", fileName);
    }
}
