package cl.ucn.disc.dam.twinpic.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Entidad base
 *
 * @author Diego P. Urrutia Astorga
 * @version 20151026
 */
// @EqualsAndHashCode(of = "id", callSuper = false)

public abstract class BaseModel extends com.raizlabs.android.dbflow.structure.BaseModel {

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {

        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);

    }

}
