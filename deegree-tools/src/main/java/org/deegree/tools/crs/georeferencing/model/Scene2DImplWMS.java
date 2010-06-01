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
package org.deegree.tools.crs.georeferencing.model;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point2d;

import org.deegree.commons.utils.StringUtils;
import org.deegree.coverage.raster.AbstractRaster;
import org.deegree.coverage.raster.SimpleRaster;
import org.deegree.coverage.raster.geom.RasterGeoReference;
import org.deegree.coverage.raster.geom.RasterRect;
import org.deegree.coverage.raster.io.RasterIOOptions;
import org.deegree.coverage.raster.utils.RasterFactory;
import org.deegree.cs.CRS;
import org.deegree.geometry.Envelope;
import org.deegree.protocol.wms.client.WMSClient111;

/**
 * 
 * Generates a 2D BufferedImage from a WMS request.
 * 
 * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class Scene2DImplWMS implements Scene2D {

    private AbstractRaster raster;

    private SimpleRaster subRaster;

    private SimpleRaster predictedRaster;

    private RasterGeoReference ref;

    private RasterRect rasterRect;

    // SampleResolution sampleRes;

    private RasterIOOptions options;

    private WMSClient111 wmsClient;

    private CRS srs;

    private List<String> lays;

    private String format;

    private BufferedImage generatedImage;

    private BufferedImage predictedImage;

    private Point2d transformedBounds;

    // private Point2d sample;

    private double minX, minY, maxX, maxY, size, resolution;

    private int imageWidth, imageHeight;

    @Override
    public void init( RasterIOOptions options, Rectangle bounds ) {
        this.options = options;
        URL url = null;

        try {
            url = new URL( options.get( "RASTER_URL" ) );

            InputStream in;

            in = url.openStream();

            raster = RasterFactory.loadRasterFromStream( in, options );
            ref = raster.getRasterReference();
            rasterRect = ref.convertEnvelopeToRasterCRS( raster.getEnvelope() );
            in.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        wmsClient = new WMSClient111( url );
        size = Double.parseDouble( options.get( "RESOLUTION" ) ) * resolution;

        transformedBounds = transformProportion( bounds, size, rasterRect );
        // sample = getSample( bounds, size, raster.getEnvelope() );
        // sampleRes = new SampleResolution( new double[] { sample.x, sample.y } );

        System.out.println( "transformedBounds: " + transformedBounds );
        System.out.println( "rasterRect: " + rasterRect + " " + ref.getResolutionX() + " " + ref.getResolutionY() );
        lays = getLayers( options );
        format = options.get( "RASTERIO_WMS_DEFAULT_FORMAT" );
        srs = options.getCRS();
        imageWidth = Integer.parseInt( options.get( "RASTERIO_WMS_MAX_WIDTH" ) );
        imageHeight = Integer.parseInt( options.get( "RASTERIO_WMS_MAX_HEIGHT" ) );

        minX = transformedBounds.x;
        minY = transformedBounds.y;

    }

    /**
     * The GetMap()-request to a WMSClient.
     * 
     * @param imageWidth
     * @param iamgeHeight
     * @param sightWindowMinX
     * @param sightWindowMaxX
     * @param sightWindowMinY
     * @param sightWindowMaxY
     * @return
     */
    private BufferedImage generateMap( Envelope imageBoundingbox ) {

        try {

            return wmsClient.getMap( lays, imageWidth, imageHeight, imageBoundingbox, srs, format, true, false, 1000,
                                     false, null ).first;

        } catch ( IOException e ) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Determines the ratio the boundingbox has to orient on. If there is a mismatch between the width and height this
     * should influence the display of the image returned by the WMS to prevent any deformation. <li>pos - orientation
     * on width because width is larger</li> <li>neg - orientation on hight because hight is larger</li> <li>other -
     * orientation on width/hight because they are even</li>
     * 
     * @param panelBounds
     *            the rectangle bounds, not <Code>null</Code>
     * @return an positive, negative or even integer
     */
    private Point2d transformProportion( Rectangle panelBounds, double size, RasterRect rect ) {
        double w = panelBounds.width;
        double h = panelBounds.height;

        double ratio = w / h;

        if ( ratio < 1 ) {
            // if < 1 then do orientation on h
            double newWidth = ( w / h ) * size * rect.width;
            return new Point2d( newWidth, rect.height * size );
        } else if ( ratio > 1 ) {
            // if > 1 then do orientation on w
            double newHeight = ( h / w ) * size * rect.height;
            return new Point2d( rect.width * size, newHeight );
        }
        // if w = h then return 0
        return new Point2d( rect.width * size, rect.height * size );
    }

    @Override
    public BufferedImage generateImage( Point2d startPoint ) {

        if ( startPoint != null ) {

            minX += startPoint.x * ref.getResolutionX();
            minY -= startPoint.y * ref.getResolutionY();
            // double maxX = minX + transformedBounds.x;
            // double maxY = minY + transformedBounds.y;

            // System.out.println( "new subRaster: " + minX + " " + minY + ", " + maxX + " " + maxY );
            // double[] worldCoordLeftLower = ref.getWorldCoordinate( minX, maxY );
            // double[] worldCoordRightUpper = ref.getWorldCoordinate( maxX, minY );
            //
            // predictedRaster = raster.getAsSimpleRaster().getSubRaster( worldCoordLeftLower[0],
            // worldCoordLeftLower[1],
            // worldCoordRightUpper[0], worldCoordRightUpper[1] );
            //
            // // RasterGeoReference ref2 = RasterGeoReference.merger( subRaster.getRasterReference(),
            // // predictedRaster.getRasterReference() );
            // Envelope env = subRaster.getEnvelope().merge( predictedRaster.getEnvelope() );
            // // System.out.println( ref2. );
            // subRaster = predictedRaster;
            //
            // System.out.println( worldCoordLeftLower[0] + " " + worldCoordRightUpper[0] );
            // return predictedImage = generateMap( env );

        } else {
            minX = rasterRect.x;
            minY = rasterRect.y;

        }

        double maxX = minX + transformedBounds.x;
        double maxY = minY + transformedBounds.y;

        // transform to get the boundingbox coordinates
        double[] worldCoordLeftLower = ref.getWorldCoordinate( minX, maxY );
        double[] worldCoordRightUpper = ref.getWorldCoordinate( maxX, minY );

        System.out.println( "minmax: " + minX + " " + minY + " " + maxX + " " + maxY );

        subRaster = raster.getAsSimpleRaster().getSubRaster( worldCoordLeftLower[0], worldCoordLeftLower[1],
                                                             worldCoordRightUpper[0], worldCoordRightUpper[1] );
        subRaster.setCoordinateSystem( raster.getCoordinateSystem() );
        System.out.println( "subRaster: " + subRaster );
        return generatedImage = generateMap( subRaster.getEnvelope() );

    }

    @Override
    public void generatePredictedImage( Point2d changePoint ) {

        double minX = changePoint.x * 2 * ref.getResolutionX();
        double minY = changePoint.y * 2 * ref.getResolutionY();
        Point2d predictedBounds = new Point2d( transformedBounds.x * 2, transformedBounds.y * 2 );
        double maxX = minX + predictedBounds.x;
        double maxY = minY + predictedBounds.y;
        // double minX = 0;
        // double minY = 0;
        // double[] max = ref.getWorldCoordinate( raster.getColumns(), raster.getRows() );

        // System.out.println( ref.getSize( raster.getEnvelope() ) );
        // System.out.println( max[0] + " " + max[1] );

        // transform to get the boundingbox coordinates
        double[] worldCoordLeftLower = ref.getWorldCoordinate( minX, maxY );
        double[] worldCoordRightUpper = ref.getWorldCoordinate( maxX, minY );

        SimpleRaster predRaster = raster.getAsSimpleRaster().getSubRaster( worldCoordLeftLower[0],
                                                                           worldCoordLeftLower[1],
                                                                           worldCoordRightUpper[0],
                                                                           worldCoordRightUpper[1] );
        System.out.println( "predictedRaster: " + predRaster );
        // System.out.println( "world: " + worldCoordLeftLower[0] + " " + worldCoordLeftLower[1] + " "
        // + worldCoordRightUpper[0] + " " + worldCoordRightUpper[1] );

    }

    /**
     * Based on bounds from an upper component this method normalizes the bounds of the upper component regarding to an
     * envelope to one pixel.
     * <p>
     * Sets the relation between panelBounds as the rectangle of the panel and bbox as the envelope of the requested
     * image.
     * 
     * @param panelBounds
     *            the rectangle bounds, not <Code>null</Code>
     * @param bbox
     *            the boundingbox, not <Code>null</Code>
     * @return a point, not <Code>null</Code>
     */
    private Point2d getSample( Rectangle panelBounds, double size, Envelope bbox ) {

        double w = panelBounds.width;
        double h = panelBounds.height;
        double oneX = bbox.getSpan0() / w;
        double oneY = bbox.getSpan1() / h;
        return new Point2d( oneX * size, oneY * size );
    }

    @Override
    public void reset() {
        generatedImage = null;
        transformedBounds = null;
    }

    @Override
    public BufferedImage getGeneratedImage() {
        return generatedImage;
    }

    /**
     * @param options
     */
    private List<String> getLayers( RasterIOOptions options ) {
        List<String> configuredLayers = new LinkedList<String>();
        String layers = options.get( "RASTERIO_WMS_REQUESTED_LAYERS" );
        if ( StringUtils.isSet( layers ) ) {
            String[] layer = layers.split( "," );
            for ( String l : layer ) {
                configuredLayers.add( l );
            }
        }
        if ( configuredLayers.isEmpty() ) {
            List<String> namedLayers = this.wmsClient.getNamedLayers();
            if ( namedLayers != null ) {
                configuredLayers.addAll( namedLayers );
            }
        }
        return configuredLayers;
    }

    @Override
    public void setResolution( double resolution ) {
        this.resolution = resolution;

    }

    @Override
    public BufferedImage getPredictedImage() {

        return predictedImage;
    }

}
