package org.onehippo.forge.exdocpicker.impl.field.tree;

import java.io.Serializable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.Model;

public class FooProvider implements ITreeProvider<Foo> {

    private static final long serialVersionUID = 1L;

    private static List<Foo> foos = new ArrayList<>();

    static {
        Foo fooA = new Foo("A");
        {
            Foo fooAA = new Foo(fooA, "AA");
            {
                new Foo(fooAA, "AAA");
                new Foo(fooAA, "AAB");
            }
            Foo fooAB = new Foo(fooA, "AB");
            {
                new Foo(fooAB, "ABA");
                Foo fooABB = new Foo(fooAB, "ABB");
                {
                    new Foo(fooABB, "ABBA");
                    Foo fooABBB = new Foo(fooABB, "ABBB");
                    {
                        new Foo(fooABBB, "ABBBA");
                    }
                }
                new Foo(fooAB, "ABC");
                new Foo(fooAB, "ABD");
            }
            Foo fooAC = new Foo(fooA, "AC");
            {
                new Foo(fooAC, "ACA");
                new Foo(fooAC, "ACB");
            }
        }
        foos.add(fooA);

        Foo fooB = new Foo("B");
        {
            new Foo(fooB, "BA");
            new Foo(fooB, "BB");
        }
        foos.add(fooB);

        Foo fooC = new Foo("C");
        foos.add(fooC);
    }

    /**
     * Construct.
     */
    public FooProvider() {
    }

    /**
     * Nothing to do.
     */
    @Override
    public void detach() {
    }

    @Override
    public Iterator<Foo> getRoots() {
        return foos.iterator();
    }

    @Override
    public boolean hasChildren(Foo foo) {
        return foo.getParent() == null || !foo.getFoos().isEmpty();
    }

    @Override
    public Iterator<Foo> getChildren(final Foo foo) {
        return foo.getFoos().iterator();
    }

    /**
     * Creates a {@link FooModel}.
     */
    @Override
    public IModel<Foo> model(Foo foo) {
        return new FooModel(foo);
    }

    /**
     * A {@link Model} which uses an id to load its {@link Foo}.
     * 
     * If {@link Foo}s were {@link Serializable} you could just use a standard {@link Model}.
     * 
     * @see #equals(Object)
     * @see #hashCode()
     */
    private static class FooModel extends LoadableDetachableModel<Foo> {
        private static final long serialVersionUID = 1L;

        private final String id;

        public FooModel(Foo foo) {
            super(foo);

            id = foo.getId();
        }

        @Override
        protected Foo load() {
            return getFoo(id);
        }

        public Foo getFoo(String id) {
            return findFoo(foos, id);
        }

        private Foo findFoo(List<Foo> foos, String id) {
            for (Foo foo : foos) {
                if (foo.getId().equals(id)) {
                    return foo;
                }

                Foo temp = findFoo(foo.getFoos(), id);
                if (temp != null) {
                    return temp;
                }
            }

            return null;
        }

        /**
         * Important! Models must be identifyable by their contained object.
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof FooModel) {
                return ((FooModel) obj).id.equals(id);
            }
            return false;
        }

        /**
         * Important! Models must be identifyable by their contained object.
         */
        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }
}