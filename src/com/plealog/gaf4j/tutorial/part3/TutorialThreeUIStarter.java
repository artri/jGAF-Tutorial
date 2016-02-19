/*  Copyright 2003-2016 Patrick G. Durand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.plealog.gaf4j.tutorial.part3;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.plealog.genericapp.api.EZApplicationBranding;
import com.plealog.genericapp.api.EZEnvironment;
import com.plealog.genericapp.api.EZGenericApplication;
import com.plealog.genericapp.api.EZSplashScreen;
import com.plealog.genericapp.api.EZSplashScreenFactory;
import com.plealog.genericapp.api.EZUIStarterListener;
import com.plealog.genericapp.api.file.EZFileManager;
import com.plealog.genericapp.api.log.EZLogger;

public class TutorialThreeUIStarter {
  public static void main(String[] args) {
    // This has to be done at the very beginning, i.e. first method call within
    // main().
    EZGenericApplication.initialize("TutorialThreeUIStarter");

    // Add application branding
    EZApplicationBranding.setAppName("TutorialThreeUIStarter");
    EZApplicationBranding.setAppVersion("1.0");
    EZApplicationBranding.setCopyRight("Created by me");
    EZApplicationBranding.setProviderName("Plealog Software");

    // We tell JRE where to locate resources... (always use a class located in
    // the same
    // package as the resources)
    EZEnvironment.addResourceLocator(TutorialThreeUIStarter.class);

    // ...before any attempt to load a resource
    EZApplicationBranding.setAppIcon(EZEnvironment.getImageIcon("appicon.png"));

    // we load our ResourceBundle containing the main menu declarations
    ResourceBundle rb = ResourceBundle.getBundle(TutorialThreeUIStarter.class
        .getPackage().getName() + ".menu");
    EZEnvironment.setUserDefinedActionsResourceBundle(rb);

    //we load our message resource bundle
    rb = ResourceBundle.getBundle(TutorialThreeUIStarter.class
        .getPackage().getName() + ".messages");
    EZEnvironment.setUserDefinedMessagesResourceBundle(rb);

    // install the action manager listener to easily handle actions
    EZEnvironment.getActionsManager().addActionMenuListener(
        new MyActionManager());

    // Add a listener to application startup cycle (see below)
    EZEnvironment.setUIStarterListener(new MyStarterListener());

    // Start the application
    EZGenericApplication.startApplication(args);
  }

  private static class MyStarterListener implements EZUIStarterListener {
    private EZSplashScreen splash;

    @Override
    public Component getApplicationComponent() {
      // This method is called by the framework to obtain the UI main component
      // to be
      // displayed in the main frame.

      JPanel mainPanel = new JPanel(new BorderLayout());
      JTabbedPane tabPanel = new JTabbedPane();

      tabPanel.add("My First Component", new JPanel());

      mainPanel.add(tabPanel, BorderLayout.CENTER);
      return mainPanel;
    }

    @Override
    public boolean isAboutToQuit() {
      // You can add some code to figure out if application can exit.

      // Return false to prevent application from exiting (e.g. a background
      // task is still running).
      // Return true otherwise.

      // Do not add a Quit dialogue box to ask user confirmation: the framework
      // already does that
      // for you.
      return true;
    }

    @Override
    public void postStart() {
      // This method is called by the framework just before displaying UI (main
      // frame).

      // we close the splash screen just before main frame becomes visible
      splash.finish();
    }

    @Override
    public void preStart() {
      // This method is called by the framework at the very beginning of
      // application startup.

      // we start a splash screen
      splash = EZSplashScreenFactory.startSplashSreen(
          EZEnvironment.getImageIcon("appbanner.png"), true);

      // we simulate some initialization work with a simple for loop that also
      // increments
      // splash screen progress bar and message field
      for (int i = 20; i <= 100; i += 20) {
        splash.setProgressPercent(i);
        splash.setMessage("Step " + ((i / 20)));
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
        }
      }
    }
  }

  /*
   * Show how to work with generic action (see ui.properties for the definition
   * of action FileOpen)
   */
  private static class MyActionManager implements PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent event) {
      if (event.getPropertyName().equals("FileOpen")) {
        File f;

        f = EZFileManager.chooseFileForOpenAction();
        if (f != null)
          EZLogger.info("Chosen file is: " + f.getAbsolutePath());

        //how to enable/disable an action:
        //EZEnvironment.getActionsManager().getAction("ShowHelp").setEnabled(false);
      }
      else if (event.getPropertyName().equals("Cmd1")) {
        EZEnvironment.displayInfoMessage(EZEnvironment.getParentFrame(), "my information message");
      }
      else if (event.getPropertyName().equals("Cmd2")) {
        EZEnvironment.displayWarnMessage(EZEnvironment.getParentFrame(), "my warning message");
      }
      else if (event.getPropertyName().equals("Cmd3")) {
        EZEnvironment.displayErrorMessage(EZEnvironment.getParentFrame(), "my error message");
      }
      else if (event.getPropertyName().equals("Cmd4")) {
        EZEnvironment.confirmMessage(EZEnvironment.getParentFrame(), "please, confirm ?");
      }
      else if (event.getPropertyName().equals("Cmd5")) {
        //this sample code also shows how to use the message resource bundle located in the package
        String value = EZEnvironment.inputValueMessage(EZEnvironment.getParentFrame(), 
            EZEnvironment.getMessage("msg1"));
        EZLogger.info(EZEnvironment.getMessage("msg2") + " " + value);
      }
      // The framework provides default behavior for Exit, About and
      // Preferences.
      // Override them by using your own dedicated code if default behavior does
      // not meet your needs.
      else if (event.getPropertyName().equals("ExitApp")) {
        EZEnvironment.getActionsManager().getDefaultActionHandler().handleExit();
      } else if (event.getPropertyName().equals("AboutApp")) {
        EZEnvironment.getActionsManager().getDefaultActionHandler().handleAbout();
      } else if (event.getPropertyName().equals("Preferences")) {
        EZEnvironment.getActionsManager().getDefaultActionHandler().handlePreferences();
      }
    }

  }
}
