/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
 
package com.barteo.emulator.device;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Command;

import com.barteo.emulator.Button;
import com.barteo.emulator.SoftButton;
import nanoxml.XMLElement;


public class Device {

  static Device instance = new Device();

  boolean initialized = false;
  Vector deviceButtons = new Vector();

  public static Color backgroundColor;
  public static Color foregroundColor;

  public static Rectangle deviceRectangle;

  public static Rectangle screenRectangle;
  public static Rectangle screenPaintable;

  public static Rectangle keyboardRectangle;
  
  
  private Device()
  {
    String xml = "";
    // Here should be device.xml but Netscape security manager doesn't accept this extension
    DataInputStream dis = new DataInputStream(
        getClass().getResourceAsStream("/com/barteo/emulator/device/device.txt"));
    try {
      while (dis.available() > 0) {
        byte[] b = new byte[dis.available()];
        dis.read(b);
        xml += new String(b);
      }
    } catch (Exception ex) {
      System.out.println("Cannot find com.barteo.emulator.device.device.txt definition file");
    }

    XMLElement doc = new XMLElement();
    doc.parseString(xml);

    for (Enumeration e = doc.enumerateChildren(); e.hasMoreElements(); ) {
      XMLElement tmp = (XMLElement) e.nextElement();
      if (tmp.getName().equals("rectangle")) {
        deviceRectangle = getRectangle(tmp);
      }
      if (tmp.getName().equals("screen")) {
        for (Enumeration e_screen = tmp.enumerateChildren(); e_screen.hasMoreElements(); ) {
          XMLElement tmp_screen = (XMLElement) e_screen.nextElement();
          if (tmp_screen.getName().equals("background")) {
            backgroundColor = new Color(Integer.parseInt(tmp_screen.getContent(), 16));
          }
          if (tmp_screen.getName().equals("foreground")) {
            foregroundColor = new Color(Integer.parseInt(tmp_screen.getContent(), 16));
          }
          if (tmp_screen.getName().equals("rectangle")) {
            screenRectangle = getRectangle(tmp_screen);
          }
          if (tmp_screen.getName().equals("paintable")) {
            screenPaintable = getRectangle(tmp_screen);
          }
        }
      }
      if (tmp.getName().equals("keyboard")) {
        for (Enumeration e_keyboard = tmp.enumerateChildren(); e_keyboard.hasMoreElements(); ) {
          XMLElement tmp_keyboard = (XMLElement) e_keyboard.nextElement();
          if (tmp_keyboard.getName().equals("rectangle")) {
            keyboardRectangle = getRectangle(tmp_keyboard);
          }
          if (tmp_keyboard.getName().equals("button")) {
            for (Enumeration e_button = tmp_keyboard.enumerateChildren(); e_button.hasMoreElements(); ) {
              XMLElement tmp_button = (XMLElement) e_button.nextElement();
              if (tmp_button.getName().equals("rectangle")) {
                deviceButtons.addElement(new Button(tmp_keyboard.getStringAttribute("name"), 
                    getRectangle(tmp_button), tmp_keyboard.getStringAttribute("key")));
              }
            }
          }
          if (tmp_keyboard.getName().equals("softbutton")) {
            Vector commands = new Vector();
            Rectangle rectangle = null, paintable = null;
            for (Enumeration e_button = tmp_keyboard.enumerateChildren(); e_button.hasMoreElements(); ) {
              XMLElement tmp_button = (XMLElement) e_button.nextElement();
              if (tmp_button.getName().equals("rectangle")) {
                rectangle = getRectangle(tmp_button);
              }
              if (tmp_button.getName().equals("paintable")) {
                paintable = getRectangle(tmp_button);
              }
              if (tmp_button.getName().equals("command")) {
                commands.addElement(tmp_button.getContent());
              }
            }
            String tmp_str = tmp_keyboard.getStringAttribute("menuactivate");
            boolean menuactivate = false;
            if (tmp_str != null && tmp_str.equals("true")) {
              menuactivate = true;
            }
            deviceButtons.addElement(new SoftButton(tmp_keyboard.getStringAttribute("name"),
                rectangle, tmp_keyboard.getStringAttribute("key"), paintable, 
                tmp_keyboard.getStringAttribute("alignment"), commands, menuactivate));
          }
        }
      }
    }
    
    initialized = true;
  }
  
  
  public static Device getInstance()
  {
    return instance;
  }


  public boolean isInitialized()
  {    
    return initialized;
  }


  public static Vector getDeviceButtons() 
  {
    return instance.deviceButtons;
  }
  
  
  Rectangle getRectangle(XMLElement source)
  {
    Rectangle rect = new Rectangle();
    
    for (Enumeration e_rectangle = source.enumerateChildren(); e_rectangle.hasMoreElements(); ) {
      XMLElement tmp_rectangle = (XMLElement) e_rectangle.nextElement();
      if (tmp_rectangle.getName().equals("x")) {
        rect.x = Integer.parseInt(tmp_rectangle.getContent());
      }
      if (tmp_rectangle.getName().equals("y")) {
        rect.y = Integer.parseInt(tmp_rectangle.getContent());
      }
      if (tmp_rectangle.getName().equals("width")) {
        rect.width = Integer.parseInt(tmp_rectangle.getContent());
      }
      if (tmp_rectangle.getName().equals("height")) {
        rect.height = Integer.parseInt(tmp_rectangle.getContent());
      }
    }
    
    return rect;
  }

}
