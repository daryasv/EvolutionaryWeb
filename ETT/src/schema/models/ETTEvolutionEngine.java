
package schema.models;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element ref="{}ETT-InitialPopulation"/>
 *         &lt;element ref="{}ETT-Selection"/>
 *         &lt;element ref="{}ETT-Crossover"/>
 *         &lt;element ref="{}ETT-Mutations"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "ETT-EvolutionEngine")
public class ETTEvolutionEngine {

    @XmlElement(name = "ETT-InitialPopulation", required = true)
    protected ETTInitialPopulation ettInitialPopulation;
    @XmlElement(name = "ETT-Selection", required = true)
    protected ETTSelection ettSelection;
    @XmlElement(name = "ETT-Crossover", required = true)
    protected ETTCrossover ettCrossover;
    @XmlElement(name = "ETT-Mutations", required = true)
    protected ETTMutations ettMutations;

    /**
     * Gets the value of the ettInitialPopulation property.
     * 
     * @return
     *     possible object is
     *     {@link ETTInitialPopulation }
     *     
     */
    public ETTInitialPopulation getETTInitialPopulation() {
        return ettInitialPopulation;
    }

    /**
     * Sets the value of the ettInitialPopulation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ETTInitialPopulation }
     *     
     */
    public void setETTInitialPopulation(ETTInitialPopulation value) {
        this.ettInitialPopulation = value;
    }

    /**
     * Gets the value of the ettSelection property.
     * 
     * @return
     *     possible object is
     *     {@link ETTSelection }
     *     
     */
    public ETTSelection getETTSelection() {
        return ettSelection;
    }

    /**
     * Sets the value of the ettSelection property.
     * 
     * @param value
     *     allowed object is
     *     {@link ETTSelection }
     *     
     */
    public void setETTSelection(ETTSelection value) {
        this.ettSelection = value;
    }

    /**
     * Gets the value of the ettCrossover property.
     * 
     * @return
     *     possible object is
     *     {@link ETTCrossover }
     *     
     */
    public ETTCrossover getETTCrossover() {
        return ettCrossover;
    }

    /**
     * Sets the value of the ettCrossover property.
     * 
     * @param value
     *     allowed object is
     *     {@link ETTCrossover }
     *     
     */
    public void setETTCrossover(ETTCrossover value) {
        this.ettCrossover = value;
    }

    /**
     * Gets the value of the ettMutations property.
     * 
     * @return
     *     possible object is
     *     {@link ETTMutations }
     *     
     */
    public ETTMutations getETTMutations() {
        return ettMutations;
    }

    /**
     * Sets the value of the ettMutations property.
     * 
     * @param value
     *     allowed object is
     *     {@link ETTMutations }
     *     
     */
    public void setETTMutations(ETTMutations value) {
        this.ettMutations = value;
    }

}
