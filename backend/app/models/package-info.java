
/**
 * Define the filters used on Hibernate queries on the entities of this package
 */
@FilterDefs(value = {
	@FilterDef( name = "entityRemovida", parameters = @ParamDef(name = "removido", type = "boolean"), defaultCondition = "removido = :removido" )
})

package models;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.ParamDef;