/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.editor.pom;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.maven.ide.eclipse.core.MavenLogger;
import org.maven.ide.eclipse.editor.xml.MvnIndexPlugin;
import org.maven.ide.eclipse.editor.xml.search.Packaging;
import org.maven.ide.eclipse.editor.xml.search.SearchEngine;
import org.maven.ide.eclipse.ui.dialogs.MavenMessageDialog;
import org.maven.ide.eclipse.util.Util;


/**
 * @author Eugene Kuleshov
 */
public abstract class FormUtils {
  public static final int MAX_MSG_LENGTH = 80;

  public static final String MORE_DETAILS = " (Click for more details)";

  public static void decorateHeader(FormToolkit toolkit, Form form) {
    Util.proxy(toolkit, FormTooliktStub.class).decorateFormHeading(form);
  }

  /**
   * Stub interface for API added to FormToolikt in Eclipse 3.3
   */
  private interface FormTooliktStub {
    public void decorateFormHeading(Form form);
  }

  /**
   * @param form
   * @param message
   * @param severity
   * @return
   */
  public static boolean setMessage(ScrolledForm form, String message, int severity) {
    if(message != null && message.length() > MAX_MSG_LENGTH) {
      String truncMsg = message;
      String[] lines = message.split("\n");
      if(lines.length > 0) {
        truncMsg = lines[0];
      } else {
        truncMsg = message.substring(0, MAX_MSG_LENGTH);
      }
      setMessageAndTTip(form, truncMsg + MORE_DETAILS, message, severity);
      return true;
    } else {
      setMessageAndTTip(form, message, message, severity);
      return false;
    }
  }

  public static void setMessageAndTTip(final ScrolledForm form, final String message, final String ttip,
      final int severity) {
    form.getForm().setMessage(message, severity);
    addFormTitleListeners(form, message, ttip, severity);
  }

  public static String nvl(String s) {
    return s == null ? "" : s;
  }

  public static String nvl(String s, String defaultValue) {
    return s == null ? defaultValue : s;
  }

  public static boolean isEmpty(String s) {
    return s == null || s.length() == 0;
  }

  public static boolean isEmpty(Text t) {
    return t == null || isEmpty(t.getText());
  }

  public static void setText(Text control, String text) {
    if(control != null && !control.isDisposed() && !control.getText().equals(text)) {
      control.setText(nvl(text));
      control.setSelection(nvl(text).length());
    }
  }

  public static void setText(CCombo control, String text) {
    if(control != null && !control.isDisposed() && !control.getText().equals(text)) {
      control.setText(nvl(text));
    }
  }

  public static void setButton(Button control, boolean selection) {
    if(control != null && !control.isDisposed() && control.getSelection() != selection) {
      control.setSelection(selection);
    }
  }

  public static void openHyperlink(String url) {
    if(!isEmpty(url) && (url.startsWith("http://") || url.startsWith("https://"))) {
      url = url.trim();
      try {
        IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
        IWebBrowser browser = browserSupport.createBrowser(IWorkbenchBrowserSupport.NAVIGATION_BAR
            | IWorkbenchBrowserSupport.LOCATION_BAR, url, url, url);
        browser.openURL(new URL(url));
      } catch(PartInitException ex) {
        MavenLogger.log(ex);
      } catch(MalformedURLException ex) {
        MavenLogger.log("Malformed url " + url, ex);
      }
    }
  }

  public static void setEnabled(Composite composite, boolean enabled) {
    if(composite != null && !composite.isDisposed()) {
      composite.setEnabled(enabled);
      for(Control control : composite.getChildren()) {
        if(control instanceof Combo) {
          control.setEnabled(enabled);

        } else if(control instanceof CCombo) {
          control.setEnabled(enabled);

        } else if(control instanceof Hyperlink) {
          control.setEnabled(enabled);

        } else if(control instanceof Composite) {
          setEnabled((Composite) control, enabled);

        } else {
          control.setEnabled(enabled);

        }
      }
    }
  }

  public static void setReadonly(Composite composite, boolean readonly) {
    if(composite != null) {
      for(Control control : composite.getChildren()) {
        if(control instanceof Text) {
          ((Text) control).setEditable(!readonly);

        } else if(control instanceof Combo) {
          ((Combo) control).setEnabled(!readonly);

        } else if(control instanceof CCombo) {
          ((CCombo) control).setEnabled(!readonly);

        } else if(control instanceof Button) {
          ((Button) control).setEnabled(!readonly);

        } else if(control instanceof Composite) {
          setReadonly((Composite) control, readonly);

        }
      }
    }
  }

  public static void addGroupIdProposal(final IProject project, final Text groupIdText, final Packaging packaging) {
    addCompletionProposal(groupIdText, new Searcher() {
      public Collection<String> search() throws CoreException {
        // TODO handle artifact info
        return getSearchEngine(project).findGroupIds(groupIdText.getText(), packaging, null);
      }
    });
  }

  public static void addArtifactIdProposal(final IProject project, final Text groupIdText, final Text artifactIdText, final Packaging packaging) {
    addCompletionProposal(artifactIdText, new Searcher() {
      public Collection<String> search() throws CoreException {
        // TODO handle artifact info
        return getSearchEngine(project).findArtifactIds(groupIdText.getText(), artifactIdText.getText(), packaging, null);
      }
    });
  }

  public static void addVersionProposal(final IProject project, final Text groupIdText, final Text artifactIdText, final Text versionText,
      final Packaging packaging) {
    addCompletionProposal(versionText, new Searcher() {
      public Collection<String> search() throws CoreException {
        return getSearchEngine(project).findVersions(groupIdText.getText(), //
            artifactIdText.getText(), versionText.getText(), packaging);
      }
    });
  }

  private static void cleanupMouseListeners(Control kid, int event) {
    Listener[] listeners = kid.getListeners(event);
    if(listeners != null) {
      for(Listener list : listeners) {
        kid.removeListener(event, list);
      }
    }
  }

  private static void addFormTitleListeners(final ScrolledForm form, final String message, final String ttip,
      final int severity) {
    if(ttip != null && ttip.length() > 0 && message != null && severity == IMessageProvider.ERROR) {
      final Composite head = form.getForm().getHead();
      Control[] kids = head.getChildren();
      for(Control kid : kids) {
        //want to get the title region only
        //Note: doing this instead of adding a head 'client' control because that gets put 
        //on the second line of the title, and looks broken. instead, converting the title
        //into a url
        if(kid != form && kid instanceof Canvas) {
          cleanupMouseListeners(kid, SWT.MouseUp);
          cleanupMouseListeners(kid, SWT.MouseEnter);
          cleanupMouseListeners(kid, SWT.MouseExit);
          kid.addMouseListener(new MouseAdapter() {
            public void mouseUp(MouseEvent e) {
              MavenMessageDialog.openInfo(form.getShell(), "Error info:", "The POM has the following error:", ttip);
            }
          });
          kid.addMouseTrackListener(new MouseTrackAdapter() {
            public void mouseEnter(MouseEvent e) {
              head.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_HAND));
            }

            public void mouseExit(MouseEvent e) {
              head.setCursor(null);
            }
          });
        }
      }
    } else {
      //no ttip or message, make sure old listeners are cleaned up if errs are removed
      final Composite head = form.getForm().getHead();
      Control[] kids = head.getChildren();
      for(Control kid : kids) {
        //want to get the title region only
        //Note: doing this instead of adding a head 'client' control because that gets put 
        //on the second line of the title, and looks broken. instead, converting the title
        //into a url
        if(kid != form && kid instanceof Canvas) {
          cleanupMouseListeners(kid, SWT.MouseUp);
          cleanupMouseListeners(kid, SWT.MouseEnter);
          cleanupMouseListeners(kid, SWT.MouseExit);
        }
      }
    }
  }

  public static void addClassifierProposal(final IProject project, final Text groupIdText, final Text artifactIdText, final Text versionText,
      final Text classifierText, final Packaging packaging) {
    addCompletionProposal(classifierText, new Searcher() {
      public Collection<String> search() throws CoreException {
        return getSearchEngine(project).findClassifiers(groupIdText.getText(), //
            artifactIdText.getText(), versionText.getText(), classifierText.getText(), packaging);
      }
    });
  }

//  public static void addTypeProposal(final Text groupIdText, final Text artifactIdText, final Text versionText,
//      final CCombo typeCombo, final Packaging packaging) {
//    addCompletionProposal(typeCombo, new Searcher() {
//      public Collection<String> search() {
//        return getSearchEngine().findTypes(groupIdText.getText(), //
//            artifactIdText.getText(), versionText.getText(), typeCombo.getText(), packaging);
//      }
//    });
//  }

  public static void addCompletionProposal(final Control control, final Searcher searcher) {
    FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
        FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);
    ControlDecoration decoration = new ControlDecoration(control, SWT.LEFT | SWT.TOP);
    decoration.setShowOnlyOnFocus(true);
    decoration.setDescriptionText(fieldDecoration.getDescription());
    decoration.setImage(fieldDecoration.getImage());

    IContentProposalProvider proposalProvider = new IContentProposalProvider() {
      public IContentProposal[] getProposals(String contents, int position) {
        ArrayList<IContentProposal> proposals = new ArrayList<IContentProposal>();
        try {
          for(final String text : searcher.search()) {
            proposals.add(new FormUtils.TextProposal(text));
          }
        } catch (CoreException e) {
          MavenLogger.log(e);
        }
        return proposals.toArray(new IContentProposal[proposals.size()]);
      }
    };

    IControlContentAdapter contentAdapter;
    if(control instanceof Text) {
      contentAdapter = new TextContentAdapter();
    } else {
      contentAdapter = new CComboContentAdapter();
    }

    ContentAssistCommandAdapter adapter = new ContentAssistCommandAdapter( //
        control, contentAdapter, proposalProvider, //
        ContentAssistCommandAdapter.CONTENT_PROPOSAL_COMMAND, null);
    // ContentProposalAdapter adapter = new ContentProposalAdapter(control, contentAdapter, //
    //     proposalProvider, KeyStroke.getInstance(SWT.MOD1, ' '), null);
    adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
    adapter.setPopupSize(new Point(250, 120));
    adapter.setPopupSize(new Point(250, 120));
  }

  static SearchEngine getSearchEngine(final IProject project) throws CoreException {
    return MvnIndexPlugin.getDefault().getSearchEngine(project);
  }

  public static abstract class Searcher {
    public abstract Collection<String> search() throws CoreException;
  }

  public static final class TextProposal implements IContentProposal {
    private final String text;

    public TextProposal(String text) {
      this.text = text;
    }

    public int getCursorPosition() {
      return text.length();
    }

    public String getContent() {
      return text;
    }

    public String getLabel() {
      return text;
    }

    public String getDescription() {
      return null;
    }
  }

}
