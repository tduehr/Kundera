/*******************************************************************************
 * * Copyright 2012 Impetus Infotech.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 ******************************************************************************/
package com.impetus.kundera.metadata.model.type;

import java.util.Set;

import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract implementation for <code>IdentifiableType</code>.
 *
 * @param <X> the generic type
 * @author vivek.mishra
 */
public abstract class AbstractIdentifiableType<X> extends AbstractManagedType<X> implements IdentifiableType<X>
{
    
    /** The id attribute. */
    private SingularAttribute<? super X, ?> idAttribute;

    /** The is id class. */
    private boolean isIdClass;

    /** The id class attributes. */
    private Set<SingularAttribute<? super X, ?>> idClassAttributes;

    /** The log. */
    private static Log log = LogFactory.getLog(AbstractIdentifiableType.class);

    /**
     * Instantiates a new abstract identifiable type.
     *
     * @param clazz the clazz
     * @param persistenceType the persistence type
     * @param superClazzType the super clazz type
     */
    AbstractIdentifiableType(Class<X> clazz, javax.persistence.metamodel.Type.PersistenceType persistenceType,
            AbstractIdentifiableType<? super X> superClazzType)
    {
        super(clazz, persistenceType, superClazzType);

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.persistence.metamodel.IdentifiableType#getId(java.lang.Class)
     */
    @Override
    public <Y> SingularAttribute<? super X, Y> getId(Class<Y> paramClass)
    {
        if (idAttribute != null)
        {
            if (idAttribute.getJavaType().equals(paramClass) && !isIdClass)
            {
                return (SingularAttribute<? super X, Y>) idAttribute;
            }
            else
            {
                onError();
            }
        }
        else
        {

            AbstractIdentifiableType<? super X> superType = (AbstractIdentifiableType<? super X>) getSupertype();
            if (superType != null)
            {
                return superType.getId(paramClass);
            }
        }

        onError();

        return null;

    }

   

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.persistence.metamodel.IdentifiableType#getDeclaredId(java.lang.
     * Class)
     */
    @Override
    public <Y> SingularAttribute<X, Y> getDeclaredId(Class<Y> paramClass)
    {
        if (idAttribute != null)
        {
            if (idAttribute.getJavaType().equals(paramClass) && !isIdClass)
            {
                return (SingularAttribute<X, Y>) idAttribute;
            }
        }

        onError();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.persistence.metamodel.IdentifiableType#getVersion(java.lang.Class)
     */
    @Override
    public <Y> SingularAttribute<? super X, Y> getVersion(Class<Y> paramClass)
    {
        // TODO: Versioning not yet supported.
        throw new UnsupportedOperationException("Method not supported");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.persistence.metamodel.IdentifiableType#getDeclaredVersion(java.
     * lang.Class)
     */
    @Override
    public <Y> SingularAttribute<X, Y> getDeclaredVersion(Class<Y> paramClass)
    {
        // TODO: Versioning not yet supported.
        throw new UnsupportedOperationException("Method not supported");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.persistence.metamodel.IdentifiableType#getSupertype()
     */
    @Override
    public IdentifiableType<? super X> getSupertype()
    {
        return (AbstractIdentifiableType<? super X>) super.getSuperClazzType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.persistence.metamodel.IdentifiableType#hasSingleIdAttribute()
     */
    @Override
    public boolean hasSingleIdAttribute()
    {
        return !isIdClass && idAttribute != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.persistence.metamodel.IdentifiableType#hasVersionAttribute()
     */
    @Override
    public boolean hasVersionAttribute()
    {
        log.warn("Versioning not yet supported. returning false, By default");
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.persistence.metamodel.IdentifiableType#getIdClassAttributes()
     */
    @Override
    public Set<SingularAttribute<? super X, ?>> getIdClassAttributes()
    {
        if (isIdClass)
        {
            return idClassAttributes;
        }
        throw new IllegalArgumentException("The identifiable type does not have an id class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.persistence.metamodel.IdentifiableType#getIdType()
     */
    @Override
    public Type<?> getIdType()
    {
        if (idAttribute != null && !isIdClass)
        {
            return idAttribute.getType();
        }

        return getSupertype().getIdType();
    }

    /**
     * Adds the id attribute.
     *
     * @param idAttribute the id attribute
     * @param isIdClass the is id class
     * @param idClassAttributes the id class attributes
     */
    public void addIdAttribute(SingularAttribute<? super X, ?> idAttribute, boolean isIdClass,
            Set<SingularAttribute<? super X, ?>> idClassAttributes)
    {

        this.idAttribute = idAttribute;
        this.isIdClass = isIdClass;
        this.idClassAttributes = idClassAttributes;
    }

    /**
     * On error.
     */
    private void onError()
    {
        throw new IllegalArgumentException(
                "id attribute of the given type is not declared in the identifiable type or if the identifiable type has an id class(e.g. @IdClass is in use)");
    }
}