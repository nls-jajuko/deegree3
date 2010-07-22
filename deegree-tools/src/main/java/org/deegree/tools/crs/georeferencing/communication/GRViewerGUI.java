//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.tools.crs.georeferencing.communication;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import org.deegree.rendering.r3d.opengl.display.OpenGLEventHandler;

/**
 * The <Code>GRViewerGUI</Code> class provides the client to view georeferencing tools/windows.
 * 
 * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class GRViewerGUI extends JFrame implements GUIConstants {

    private final static Dimension SUBCOMPONENT_DIMENSION = new Dimension( 1, 1 );

    private final static Dimension FRAME_DIMENSION = new Dimension( 900, 600 );

    private Scene2DPanel scenePanel2D;

    private NavigationBarPanel navigationPanel;

    private OpenGLEventHandler openGLEventListener;

    private PointTableFrame pointTablePanel;

    private BuildingFootprintPanel footprintPanel;

    private JMenuItem editMenuItem;

    private CheckBoxList checkbox;

    private JTextField coordinateJumper;

    private JMenu menuTransformation;

    public GRViewerGUI() {
        super( WINDOW_TITLE );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbl.setConstraints( this, gbc );

        setLayout( gbl );
        setMinimumSize( FRAME_DIMENSION );

        setPreferredSize( FRAME_DIMENSION );

        setupMenubar();
        setup2DScene( gbl );
        setupPanelFootprint( gbl );
        setupOpenGL( gbl, false );
        setupNavigationBar( gbl );
        setupPointTable( gbl );
        this.pack();
    }

    private void setupMenubar() {

        JMenuBar menuBar;
        JMenu menuFile;
        JMenu menuEdit;

        menuBar = new JMenuBar();
        menuFile = new JMenu( MENU_FILE );
        menuEdit = new JMenu( MENU_EDIT );
        menuTransformation = new JMenu( MENU_TRANSFORMATION );

        menuBar.add( menuFile );
        menuBar.add( menuEdit );
        menuBar.add( menuTransformation );

        editMenuItem = new JMenuItem( MENUITEM_EDIT_OPTIONS );
        String[] sArray = new String[] { MENUITEM_TRANS_POLYNOM_FIRST, MENUITEM_TRANS_HELMERT };
        checkbox = new CheckBoxList( sArray );
        menuTransformation.add( checkbox );
        menuEdit.add( editMenuItem );

        this.getRootPane().setJMenuBar( menuBar );
    }

    private void setupNavigationBar( GridBagLayout gbl ) {
        navigationPanel = new NavigationBarPanel();
        navigationPanel.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
        navigationPanel.setPreferredSize( SUBCOMPONENT_DIMENSION );

        coordinateJumper = new JTextField( 15 );
        coordinateJumper.setName( JTEXTFIELD_COORDINATE_JUMPER );
        navigationPanel.add( coordinateJumper );

        GridBagLayoutHelper.addComponent( this.getContentPane(), gbl, navigationPanel, 0, 0, 3, 1, .15, .15 );
    }

    private void setup2DScene( GridBagLayout gbl ) {
        scenePanel2D = new Scene2DPanel();
        scenePanel2D.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
        scenePanel2D.setPreferredSize( SUBCOMPONENT_DIMENSION );

        GridBagLayoutHelper.addComponent( this.getContentPane(), gbl, scenePanel2D, 0, 1, 2, 2, 1.0, 1.0 );

    }

    private void setupPanelFootprint( GridBagLayout gbl ) {

        footprintPanel = new BuildingFootprintPanel();
        footprintPanel.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
        footprintPanel.setBackground( Color.white );
        footprintPanel.setPreferredSize( SUBCOMPONENT_DIMENSION );

        GridBagLayoutHelper.addComponent( this.getContentPane(), gbl, footprintPanel, 2, 1, 1, 1,
                                          footprintPanel.getInsets(), GridBagConstraints.LINE_END, .5, 1 );

    }

    private void setupOpenGL( GridBagLayout gbl, boolean testSphere ) {
        GLCapabilities caps = new GLCapabilities();
        caps.setDoubleBuffered( true );
        caps.setHardwareAccelerated( true );
        caps.setAlphaBits( 8 );
        caps.setAccumAlphaBits( 8 );
        openGLEventListener = new OpenGLEventHandler( testSphere );

        GLCanvas canvas = new GLCanvas( caps );
        canvas.addGLEventListener( openGLEventListener );
        canvas.addMouseListener( openGLEventListener.getTrackBall() );
        canvas.addMouseWheelListener( openGLEventListener.getTrackBall() );
        canvas.addMouseMotionListener( openGLEventListener.getTrackBall() );
        canvas.setPreferredSize( SUBCOMPONENT_DIMENSION );

        GridBagLayoutHelper.addComponent( this.getContentPane(), gbl, canvas, 2, 2, 1, 1, new Insets( 0, 10, 0, 0 ),
                                          GridBagConstraints.LINE_END, .5, 1 );
    }

    private void setupPointTable( GridBagLayout gbl ) {
        pointTablePanel = new PointTableFrame();

        // pointTablePanel.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
        // pointTablePanel.setPreferredSize( SUBCOMPONENT_DIMENSION );

        // GridBagLayoutHelper.addComponent( this.getContentPane(), gbl, pointTablePanel, 1, 2, 2, 1, .5, .5 );
    }

    /**
     * not used at the moment
     */
    public void resetScene2D() {
        scenePanel2D.paint( new BufferedImage( 0, 0, BufferedImage.TYPE_3BYTE_BGR ).createGraphics() );
    }

    /**
     * Adds the actionListener to the visible components to interact with the user.
     * 
     * @param e
     */
    public void addMenuItemListener( ActionListener e ) {
        checkbox.addCheckboxListener( e );
        coordinateJumper.addActionListener( e );
        editMenuItem.addActionListener( e );

    }

    /**
     * The {@link Scene2DPanel} is a child of this Container
     * 
     * @return
     */
    public Scene2DPanel getScenePanel2D() {
        return scenePanel2D;
    }

    public BuildingFootprintPanel getFootprintPanel() {
        return footprintPanel;
    }

    public void addHoleWindowListener( ComponentListener c ) {
        this.addComponentListener( c );

    }

    public JTextField getCoordinateJumper() {
        return coordinateJumper;
    }

    public NavigationBarPanel getNavigationPanel() {
        return navigationPanel;
    }

    public PointTableFrame getPointTablePanel() {
        return pointTablePanel;
    }

    public OpenGLEventHandler getOpenGLEventListener() {
        return openGLEventListener;
    }

    /**
     * Sets everything that is needed to handle userinteraction with the checkboxes in the transformationMenu.
     * 
     * @param selectedCheckbox
     *            the checkbox that has been selected by the user.
     */
    public void activateTransformationCheckbox( JCheckBox selectedCheckbox ) {
        this.checkbox.selectThisCheckbox( selectedCheckbox );
        this.menuTransformation.getPopupMenu().setVisible( false );
        this.menuTransformation.setSelected( false );
    }

}
