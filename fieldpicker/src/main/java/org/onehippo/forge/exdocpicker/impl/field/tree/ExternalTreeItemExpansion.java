package org.onehippo.forge.exdocpicker.impl.field.tree;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;

public class ExternalTreeItemExpansion implements Set<Serializable>, Serializable
{
    private static final long serialVersionUID = 1L;

    private static MetaDataKey<ExternalTreeItemExpansion> KEY = new MetaDataKey<ExternalTreeItemExpansion>()
    {
        private static final long serialVersionUID = 1L;
    };

    private Set<String> ids = new HashSet<>();

    private boolean inverse;

    public void expandAll()
    {
        ids.clear();

        inverse = true;
    }

    public void collapseAll()
    {
        ids.clear();

        inverse = false;
    }

    @Override
    public boolean add(Serializable foo)
    {
        // FIXME
        return false;
//        if (inverse)
//        {
//            return ids.remove(foo.getId());
//        }
//        else
//        {
//            return ids.add(foo.getId());
//        }
    }

    @Override
    public boolean remove(Object o)
    {
        // FIXME
        return false;
//        Foo foo = (Foo)o;
//
//        if (inverse)
//        {
//            return ids.add(foo.getId());
//        }
//        else
//        {
//            return ids.remove(foo.getId());
//        }
    }

    @Override
    public boolean contains(Object o)
    {
        // FIXME
        return false;
//        Foo foo = (Foo)o;
//
//        if (inverse)
//        {
//            return !ids.contains(foo.getId());
//        }
//        else
//        {
//            return ids.contains(foo.getId());
//        }
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <A> A[] toArray(A[] a)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Serializable> iterator()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Serializable> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the expansion for the session.
     * 
     * @return expansion
     */
    public static ExternalTreeItemExpansion get()
    {
        ExternalTreeItemExpansion expansion = Session.get().getMetaData(KEY);
        if (expansion == null)
        {
            expansion = new ExternalTreeItemExpansion();

            Session.get().setMetaData(KEY, expansion);
        }
        return expansion;
    }
}