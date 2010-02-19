//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-792 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.02.19 at 03:09:15 PM MEZ 
//


package org.deegree.commons.datasource.configuration;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * 
 *         A WMS datasource defines the access to a 'remote' wms. For
 *         this purpose it holds information about the request type (GetMap,
 *         GetFeatureInfo)
 *         and the layers.
 *       
 * 
 * <p>Java class for WMSDataSourceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WMSDataSourceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.deegree.org/datasource}AbstractWebBasedDataSourceType">
 *       &lt;sequence>
 *         &lt;element name="RequestedLayers" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RequestedFormat" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="transparent" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="RequestTimeout" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="MaxMapDimensions" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *                 &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DefaultSRS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RestrictedRequestParameter" type="{http://www.deegree.org/datasource}RestrictedRequestParameterType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WMSDataSourceType", propOrder = {
    "requestedLayers",
    "requestedFormat",
    "requestTimeout",
    "maxMapDimensions",
    "defaultSRS",
    "restrictedRequestParameter"
})
public class WMSDataSourceType
    extends AbstractWebBasedDataSourceType
{

    @XmlElement(name = "RequestedLayers", required = true)
    protected String requestedLayers;
    @XmlElement(name = "RequestedFormat")
    protected WMSDataSourceType.RequestedFormat requestedFormat;
    @XmlElement(name = "RequestTimeout", defaultValue = "60")
    protected BigInteger requestTimeout;
    @XmlElement(name = "MaxMapDimensions")
    protected WMSDataSourceType.MaxMapDimensions maxMapDimensions;
    @XmlElement(name = "DefaultSRS")
    protected String defaultSRS;
    @XmlElement(name = "RestrictedRequestParameter")
    protected List<RestrictedRequestParameterType> restrictedRequestParameter;

    /**
     * Gets the value of the requestedLayers property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestedLayers() {
        return requestedLayers;
    }

    /**
     * Sets the value of the requestedLayers property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestedLayers(String value) {
        this.requestedLayers = value;
    }

    /**
     * Gets the value of the requestedFormat property.
     * 
     * @return
     *     possible object is
     *     {@link WMSDataSourceType.RequestedFormat }
     *     
     */
    public WMSDataSourceType.RequestedFormat getRequestedFormat() {
        return requestedFormat;
    }

    /**
     * Sets the value of the requestedFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link WMSDataSourceType.RequestedFormat }
     *     
     */
    public void setRequestedFormat(WMSDataSourceType.RequestedFormat value) {
        this.requestedFormat = value;
    }

    /**
     * Gets the value of the requestTimeout property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRequestTimeout() {
        return requestTimeout;
    }

    /**
     * Sets the value of the requestTimeout property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRequestTimeout(BigInteger value) {
        this.requestTimeout = value;
    }

    /**
     * Gets the value of the maxMapDimensions property.
     * 
     * @return
     *     possible object is
     *     {@link WMSDataSourceType.MaxMapDimensions }
     *     
     */
    public WMSDataSourceType.MaxMapDimensions getMaxMapDimensions() {
        return maxMapDimensions;
    }

    /**
     * Sets the value of the maxMapDimensions property.
     * 
     * @param value
     *     allowed object is
     *     {@link WMSDataSourceType.MaxMapDimensions }
     *     
     */
    public void setMaxMapDimensions(WMSDataSourceType.MaxMapDimensions value) {
        this.maxMapDimensions = value;
    }

    /**
     * Gets the value of the defaultSRS property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultSRS() {
        return defaultSRS;
    }

    /**
     * Sets the value of the defaultSRS property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultSRS(String value) {
        this.defaultSRS = value;
    }

    /**
     * Gets the value of the restrictedRequestParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the restrictedRequestParameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRestrictedRequestParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RestrictedRequestParameterType }
     * 
     * 
     */
    public List<RestrictedRequestParameterType> getRestrictedRequestParameter() {
        if (restrictedRequestParameter == null) {
            restrictedRequestParameter = new ArrayList<RestrictedRequestParameterType>();
        }
        return this.restrictedRequestParameter;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}integer" />
     *       &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}integer" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class MaxMapDimensions {

        @XmlAttribute
        protected BigInteger width;
        @XmlAttribute
        protected BigInteger height;

        /**
         * Gets the value of the width property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getWidth() {
            return width;
        }

        /**
         * Sets the value of the width property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setWidth(BigInteger value) {
            this.width = value;
        }

        /**
         * Gets the value of the height property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getHeight() {
            return height;
        }

        /**
         * Sets the value of the height property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setHeight(BigInteger value) {
            this.height = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attribute name="transparent" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class RequestedFormat {

        @XmlValue
        protected String value;
        @XmlAttribute
        protected Boolean transparent;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Gets the value of the transparent property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isTransparent() {
            return transparent;
        }

        /**
         * Sets the value of the transparent property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setTransparent(Boolean value) {
            this.transparent = value;
        }

    }

}
