/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package jade.content.schema;

import jade.content.abs.*;
import jade.content.onto.OntologyException;

/**
 * @author Federico Bergenti - Universita` di Parma
 */
public class AggregateSchema extends TermSchema {
    public static final String         BASE_NAME = "Aggregate";
    private static AggregateSchema baseSchema = new AggregateSchema();

    /**
     * Construct a schema that vinculates an entity to be a generic
     * aggregate
     */
    private AggregateSchema() {
        super(BASE_NAME);
    }

    /**
     * Creates an <code>AggregateSchema</code> with a given type-name.
     *
     * @param typeName The name of this <code>AggregateSchema</code>.
     */
    public AggregateSchema(String typeName) {
        super(typeName);
    }

    /**
     * Retrieve the generic base schema for all aggregates.
     *
     * @return the generic base schema for all aggregates.
     */
    public static ObjectSchema getBaseSchema() {
        return baseSchema;
    } 
    
    /**
     * Creates an Abstract descriptor to hold an aggregate of
     * the proper type.
     */
    public AbsObject newInstance() throws OntologyException {
        return new AbsAggregate(getTypeName());
    } 

}
