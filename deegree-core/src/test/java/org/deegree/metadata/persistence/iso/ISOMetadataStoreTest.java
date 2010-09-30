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
package org.deegree.metadata.persistence.iso;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;

import org.apache.axiom.om.OMElement;
import org.deegree.CoreTstProperties;
import org.deegree.commons.jdbc.ConnectionManager;
import org.deegree.commons.xml.XMLAdapter;
import org.deegree.metadata.persistence.MetadataResultSet;
import org.deegree.metadata.persistence.MetadataStoreException;
import org.deegree.metadata.persistence.MetadataStoreTransaction;
import org.deegree.metadata.publication.InsertTransaction;
import org.deegree.protocol.csw.CSWConstants;
import org.deegree.protocol.csw.CSWConstants.OutputSchema;
import org.deegree.protocol.csw.CSWConstants.ReturnableElement;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ISOMetadataStoreTest {

    private static final Logger LOG = getLogger( ISOMetadataStoreTest.class );

    private static final URL configURL = ISOMetadataStoreTest.class.getResource( "iso19115_SetUpTables.xml" );

    private static final URL configURL_REJECT_FI_FALSE = ISOMetadataStoreTest.class.getResource( "iso19115_Reject_FI_FALSE.xml" );

    private static final URL configURL_REJECT_FI_TRUE = ISOMetadataStoreTest.class.getResource( "iso19115_Reject_FI_TRUE.xml" );

    private static final URL configURL_RS_GEN_FALSE = ISOMetadataStoreTest.class.getResource( "iso19115_RS_Available_Generate_FALSE.xml" );

    private static final URL configURL_RS_GEN_TRUE = ISOMetadataStoreTest.class.getResource( "iso19115_RS_Available_Generate_TRUE.xml" );

    private ISOMetadataStore store;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp()
                            throws Exception {
        String jdbcURL = CoreTstProperties.getProperty( "iso_store_url" );
        String jdbcUser = CoreTstProperties.getProperty( "iso_store_user" );
        String jdbcPass = CoreTstProperties.getProperty( "iso_store_pass" );
        if ( jdbcURL != null && jdbcUser != null && jdbcPass != null ) {
            Set<String> connIds = ConnectionManager.getConnectionIds();
            if ( connIds.contains( "iso_pg_set_up_tables" ) ) {
                // skip new creation of the connection
                deleteFromTables( ConnectionManager.getConnection( "iso_pg_set_up_tables" ) );
            } else {
                ConnectionManager.addConnection( "iso_pg_set_up_tables", jdbcURL, jdbcUser, jdbcPass, 5, 20 );
                Connection connSetUpTables = null;

                try {
                    connSetUpTables = ConnectionManager.getConnection( "iso_pg_set_up_tables" );
                    setUpTables( connSetUpTables );
                } finally {
                    connSetUpTables.close();
                }

            }

        }
    }

    private void setUpTables( Connection conn )
                            throws SQLException, UnsupportedEncodingException, IOException, MetadataStoreException {

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            for ( String sql : new ISOMetadataStoreProvider().getDropStatements( configURL ) ) {
                stmt.executeUpdate( sql );
            }
            for ( String sql : new ISOMetadataStoreProvider().getCreateStatements( configURL ) ) {
                stmt.execute( sql );
            }

        } finally {
            if ( stmt != null ) {
                stmt.close();
            }
        }
    }

    private void deleteFromTables( Connection conn )
                            throws SQLException, UnsupportedEncodingException, IOException {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = "DELETE from datasets;";
            stmt.executeUpdate( sql );

        } finally {
            if ( stmt != null ) {
                stmt.close();
            }
        }
    }

    @Test
    public void testInsert()
                            throws MetadataStoreException, XMLStreamException, FactoryConfigurationError, IOException {
        store = (ISOMetadataStore) new ISOMetadataStoreProvider().getMetadataStore( configURL );
        if ( store == null ) {
            LOG.warn( "Skipping test (needs configuration)." );
            return;
        }
        MetadataStoreTransaction ta = store.acquireTransaction();
        List<OMElement> records;
        int countInserted = 0;
        int countInsert = 0;

        File folder = new File( "/home/thomas/Dokumente/metadata/test" );
        File[] fileArray = folder.listFiles();
        if ( fileArray != null ) {
            countInsert = fileArray.length;
            System.out.println( "TEST: arraySize: " + countInsert );
            for ( File f : fileArray ) {
                records = new ArrayList<OMElement>();
                OMElement record = new XMLAdapter( f ).getRootElement();
                // MetadataRecord record = loadRecord( url );
                LOG.info( "inserting filename: " + f.getName() );
                records.add( record );
                InsertTransaction insert = new InsertTransaction( records, records.get( 0 ).getQName(), "insert" );
                List<String> ids = ta.performInsert( insert );
                if ( !ids.isEmpty() ) {
                    countInserted += ids.size();
                }
                ta.commit();
            }
        }
        LOG.info( countInserted + " from " + countInsert + " Metadata inserted." );

        // TODO test various queries

    }

    // @Test
    // public void testGetRecord()
    // throws MetadataStoreException {
    //
    // File file = new File( "/home/thomas/Dokumente/metadata/testCases/1.xml" );
    // List<String> ids = insertOneMetadata( file );
    //
    // MetadataResultSet resultSet = store.getRecordsById(
    // ids,
    // CSWConstants.OutputSchema.determineOutputSchema( OutputSchema.ISO_19115 ),
    // ReturnableElement.full );
    // // LOG.info( "" + resultSet.getResultType().getNumberOfRecordsMatched() );
    //
    // Assert.assertEquals( 1, resultSet.getMembers().size() );
    //
    // // TODO test various queries
    // }

    /**
     * If the fileIdentifier should be generated automaticaly if not set.
     * <p>
     * 1.xml has no fileIdentifier<br>
     * 2.xml has a fileIdentifier
     * 
     * @throws MetadataStoreException
     */
    @Test
    public void testIdentifierRejectFalse()
                            throws MetadataStoreException {
        store = (ISOMetadataStore) new ISOMetadataStoreProvider().getMetadataStore( configURL_REJECT_FI_FALSE );

        File file = new File( "/home/thomas/Dokumente/metadata/testCases/1.xml" );
        File file2 = new File( "/home/thomas/Dokumente/metadata/testCases/2.xml" );

        List<String> ids = insertMetadata( store, file, file2 );

        MetadataResultSet resultSet = store.getRecordsById(
                                                            ids,
                                                            CSWConstants.OutputSchema.determineOutputSchema( OutputSchema.ISO_19115 ),
                                                            ReturnableElement.full );

        Assert.assertEquals( 2, resultSet.getMembers().size() );

    }

    /**
     * If the fileIdentifier shouldn't be generated automaticaly if not set.
     * <p>
     * 1.xml has no fileIdentifier but with one ResourceIdentifier -> insert<br>
     * 2.xml has a fileIdentifier -> insert Output: 2 because 1.xml has a resourceIdentifier which can be taken
     * 
     * @throws MetadataStoreException
     */
    @Test
    public void testIdentifierRejectTrue()
                            throws MetadataStoreException {
        store = (ISOMetadataStore) new ISOMetadataStoreProvider().getMetadataStore( configURL_REJECT_FI_TRUE );

        File file = new File( "/home/thomas/Dokumente/metadata/testCases/1.xml" );
        File file2 = new File( "/home/thomas/Dokumente/metadata/testCases/2.xml" );

        List<String> ids = insertMetadata( store, file, file2 );

        MetadataResultSet resultSet = store.getRecordsById(
                                                            ids,
                                                            CSWConstants.OutputSchema.determineOutputSchema( OutputSchema.ISO_19115 ),
                                                            ReturnableElement.full );

        Assert.assertEquals( 2, resultSet.getMembers().size() );

    }

    /**
     * If the fileIdentifier shouldn't be generated automaticaly if not set.
     * <p>
     * 1.xml has no fileIdentifier and no ResourceIdentifier -> reject<br>
     * 2.xml has a fileIdentifier -> insert <br>
     * Output should be a MetadataStoreException
     * 
     * @throws MetadataStoreException
     */
    @Test(expected = MetadataStoreException.class)
    public void testIdentifierRejectTrueWithRejection()
                            throws MetadataStoreException {
        store = (ISOMetadataStore) new ISOMetadataStoreProvider().getMetadataStore( configURL_REJECT_FI_TRUE );

        File file = new File( "/home/thomas/Dokumente/metadata/testCases/1.xml" );
        File file2 = new File( "/home/thomas/Dokumente/metadata/testCases/3.xml" );

        insertMetadata( store, file, file2 );

    }

    /**
     * If the ResourceIdentifier shouldn't be generated automaticaly and <br>
     * if there is no neither RS_ID not id-attribute set
     * <p>
     * Output should be generate a MetadataStoreException
     * 
     * @throws MetadataStoreException
     */
    @Test(expected = MetadataStoreException.class)
    public void testResourceIdentifierGenerateFALSE_NO_RS_ID()
                            throws MetadataStoreException {
        store = (ISOMetadataStore) new ISOMetadataStoreProvider().getMetadataStore( configURL_RS_GEN_FALSE );

        File file = new File( "/home/thomas/Dokumente/metadata/testCases/3.xml" );

        List<String> ids = insertMetadata( store, file );

    }

    /**
     * If the ResourceIdentifier shouldn't be generated automaticaly and <br>
     * there is no RS_ID set but id-attribute set
     * <p>
     * Output should be generate a MetadataStoreException
     * 
     * @throws MetadataStoreException
     */
    @Test(expected = MetadataStoreException.class)
    public void testResourceIdentifierGenerateFALSE_With_ID_Attrib()
                            throws MetadataStoreException {
        store = (ISOMetadataStore) new ISOMetadataStoreProvider().getMetadataStore( configURL_RS_GEN_FALSE );

        File file2 = new File( "/home/thomas/Dokumente/metadata/testCases/4.xml" );

        List<String> ids = insertMetadata( store, file2 );

    }

    /**
     * If the ResourceIdentifier shouldn't be generated automaticaly and <br>
     * there is no RS_ID set but id-attribute and uuid-attribute set
     * <p>
     * Output should be generate a MetadataStoreException
     * 
     * @throws MetadataStoreException
     */
    @Test(expected = MetadataStoreException.class)
    public void testResourceIdentifierGenerateFALSE_With_ID_UUID_Attrib()
                            throws MetadataStoreException {
        store = (ISOMetadataStore) new ISOMetadataStoreProvider().getMetadataStore( configURL_RS_GEN_FALSE );

        File file = new File( "/home/thomas/Dokumente/metadata/testCases/5.xml" );

        List<String> ids = insertMetadata( store, file );

    }

    /**
     * If the ResourceIdentifier shouldn't be generated automaticaly and <br>
     * if there is RS_ID set and id-attribute set
     * <p>
     * Output should be 1
     * 
     * @throws MetadataStoreException
     */
    @Test
    public void testResourceIdentifierGenerateFALSE_With_ID_Attrib_RSID_Equals()
                            throws MetadataStoreException {
        store = (ISOMetadataStore) new ISOMetadataStoreProvider().getMetadataStore( configURL_RS_GEN_FALSE );

        File file = new File( "/home/thomas/Dokumente/metadata/testCases/6.xml" );

        List<String> ids = insertMetadata( store, file );
        MetadataResultSet resultSet = store.getRecordsById(
                                                            ids,
                                                            CSWConstants.OutputSchema.determineOutputSchema( OutputSchema.ISO_19115 ),
                                                            ReturnableElement.full );

        Assert.assertEquals( 1, resultSet.getMembers().size() );

    }

    /**
     * If the ResourceIdentifier shouldn't be generated automaticaly and <br>
     * if there is RS_ID set and id-attribute set but not equals
     * <p>
     * Output should be generate a MetadataStoreException
     * 
     * @throws MetadataStoreException
     */
    @Test(expected = MetadataStoreException.class)
    public void testResourceIdentifierGenerateFALSE_With_ID_Attrib_RSID_NOT_Equals()
                            throws MetadataStoreException {
        store = (ISOMetadataStore) new ISOMetadataStoreProvider().getMetadataStore( configURL_RS_GEN_FALSE );

        File file = new File( "/home/thomas/Dokumente/metadata/testCases/7.xml" );

        List<String> ids = insertMetadata( store, file );

    }

    /**
     * If the ResourceIdentifier shouldn't be generated automaticaly and <br>
     * if there is RS_ID set and id-attribute set and equals but not uuid compliant
     * <p>
     * Output should be generate a MetadataStoreException
     * 
     * @throws MetadataStoreException
     */
    @Test(expected = MetadataStoreException.class)
    public void testResourceIdentifierGenerateFALSE_With_ID_Attrib_RSID_NOT_Equals_NO_UUID()
                            throws MetadataStoreException {
        store = (ISOMetadataStore) new ISOMetadataStoreProvider().getMetadataStore( configURL_RS_GEN_FALSE );

        File file = new File( "/home/thomas/Dokumente/metadata/testCases/8.xml" );

        List<String> ids = insertMetadata( store, file );

    }

    /**
     * Metadata that is false regarding the ResourceIdentifier
     * <p>
     * 3.xml has got an valid combination -> autmatic generating<br>
     * 4.xml has no valid combination -> autmatic generating <br>
     * 5.xml has no valid combination -> autmatic generating <br>
     * 6.xml has a valid combination -> so nothing should be generated <br>
     * 7.xml has no valid combination -> autmatic generating <br>
     * Output should be 5 valid metadataRecords in backend
     * 
     * @throws MetadataStoreException
     */
    @Test
    public void testResourceIdentifierGenerateTRUE()
                            throws MetadataStoreException {
        store = (ISOMetadataStore) new ISOMetadataStoreProvider().getMetadataStore( configURL_RS_GEN_TRUE );

        File file = new File( "/home/thomas/Dokumente/metadata/testCases/3.xml" );
        File file2 = new File( "/home/thomas/Dokumente/metadata/testCases/4.xml" );
        File file3 = new File( "/home/thomas/Dokumente/metadata/testCases/5.xml" );
        File file4 = new File( "/home/thomas/Dokumente/metadata/testCases/6.xml" );
        File file5 = new File( "/home/thomas/Dokumente/metadata/testCases/7.xml" );

        List<String> ids = insertMetadata( store, file, file2, file3, file4, file5 );

        MetadataResultSet resultSet = store.getRecordsById(
                                                            ids,
                                                            CSWConstants.OutputSchema.determineOutputSchema( OutputSchema.ISO_19115 ),
                                                            ReturnableElement.full );

        Assert.assertEquals( 5, resultSet.getMembers().size() );

    }

    private List<String> insertMetadata( ISOMetadataStore store, File... fileInput )
                            throws MetadataStoreException {
        if ( store == null ) {
            LOG.warn( "Skipping test (needs configuration)." );
            return null;
        }
        MetadataStoreTransaction ta = store.acquireTransaction();
        List<OMElement> records = new ArrayList<OMElement>();

        List<String> ids = new ArrayList<String>();
        for ( File file : fileInput ) {
            if ( file.isFile() ) {

                OMElement record = new XMLAdapter( file ).getRootElement();
                LOG.info( "inserting filename: " + file.getName() );
                records.add( record );

            }
        }
        InsertTransaction insert = new InsertTransaction( records, records.get( 0 ).getQName(), "insert" );
        ids = ta.performInsert( insert );
        ta.commit();
        int counter = 0;
        for ( File file : fileInput ) {
            LOG.info( file + " with id'" + ids.get( counter++ ) + "' as Metadata inserted." );
        }
        return ids;
    }

    // private MetadataRecord loadRecord( URL url )
    // throws XMLStreamException, FactoryConfigurationError, IOException {
    // XMLStreamReader xmlStream = XMLInputFactory.newInstance().createXMLStreamReader( url.openStream() );
    // return new ISORecord( xmlStream );
    // }
}
