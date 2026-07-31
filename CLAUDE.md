# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

This is the Bloomreach/Hippo CMS **External Document Picker Base** forge project: reusable building blocks for
letting CMS editors link a document field (or a folder) to items that live outside the JCR repository (e.g. rows
in an external system reachable only via REST). It ships three independent picker implementations plus a demo:

- `fieldpicker` — classic Wicket-based CMS plugin for document fields and folder context menus.
- `richtext-ckeditor` — a CKEditor 4 plugin adding an external-link picker button to the Rich Text Editor.
- `openui-fieldpicker` — a modern Angular 12 app served as a Hippo **OpenUI** extension (iframe) for document fields.
- `demo/` — a separate Maven reactor (own parent, `hippo-cms7-release`) that boots a full Hippo CMS + example REST
  backend to exercise all three pickers end to end. Not part of the root reactor's `<modules>`.

Root reactor parent: `org.onehippo.cms7:hippo-cms7-project:17.0.0`. Project Java target is **21**
(`project.build.javaVersion` in root `pom.xml`), not the org-wide default.

## Common commands

### Root reactor (fieldpicker + richtext-ckeditor + openui-fieldpicker)

```
mvn clean install                 # build/test everything (openui-fieldpicker runs its full npm build+test too)
mvn -pl fieldpicker test          # run only the Wicket/Java module's tests
mvn -pl fieldpicker test -Dtest=SimpleExternalDocumentCollectionTest   # single Java test class
```

### Documentation site

Docs live as Maven xdoc sources under `src/site/xdoc/**` and are the authoritative architecture references
(`field/architecture.xml`, `openui-field/architecture.xml`, `openui-field/spec-of-rest-services.xml`, etc.).

```
mvn clean site                    # renders to target/site/ (open target/site/index.html)
mvn -Pgithub.pages clean site     # renders into docs/ for GitHub Pages — only run from the master branch
```

### richtext-ckeditor (manual/browser testing only — no automated Java tests in this module)

The module has no unit tests; verification is manual via a Jetty server that serves the plugin source
alongside vendored CKEditor test pages under `src/test/resources/`:

```
mvn -pl richtext-ckeditor jetty:run   # serves src/main/resources + src/test/resources at http://localhost:8080/
```

### openui-fieldpicker (Angular 12, driven from Maven via frontend-maven-plugin)

Maven pins Node `v16.19.0` / npm `8.19.3` and runs `npm ci` → `npm run build-prod` → (on `mvn test`)
`npm test -- --watch=false --browsers ChromeHeadless`. `mvn clean` also removes `node/` and `node_modules/`.
This module's default Maven goal is `validate`, not `package`, so it must be built explicitly via the root
reactor or `mvn -pl openui-fieldpicker package`.

For local iteration, run npm/ng directly (respect the `engines` constraint: Node 16, npm 8/9):

```
cd openui-fieldpicker
npm ci
npm start                         # ng serve, local dev server
npm test                          # ng test — Karma/Jasmine unit tests, watches by default
npm test -- --watch=false --browsers ChromeHeadless   # matches the Maven-invoked headless run
ng test --include='**/field-view.component.spec.ts'   # run a single spec file
npm run lint                      # ng lint (TSLint)
npm run build-prod                # ng build --prod
npm run e2e                       # Protractor e2e suite
```

## Architecture

### fieldpicker: facade pattern over the Hippo plugin lifecycle

The Wicket plugin never talks to your external system directly. Everything funnels through one interface you
implement per document field / folder-workflow type:

```java
ExternalDocumentServiceFacade<T extends Serializable>
    extends ExternalDocumentSearchService<T>, ExternalDocumentFieldService<T>,
            ExternalDocumentDisplayService<T>, ExternalDocumentTreeService<T>, IClusterable
```
(`fieldpicker/.../api/ExternalDocumentServiceFacade.java`)

- `ExternalDocumentSearchService.searchExternalDocuments(context, queryString)` — runs a search, returns an
  `ExternalDocumentCollection<T>`.
- `ExternalDocumentFieldService.getFieldExternalDocuments` / `setFieldExternalDocuments` — read/write the
  currently-linked items on the CMS document node.
- `ExternalDocumentDisplayService.getDocumentTitle` / `getDocumentDescription` / `getDocumentIconLink` (locale-aware)
  and `isDocumentSelectable` — how a `T` renders in the picker dialog.
- `ExternalDocumentTreeService.hasChildren` / `getChildren` / `getParent` (all default no-op) — implement these
  only if the picker should render a **Tree List View** instead of the default **Flat List View**.
- `ExternalDocumentCollection<T>` is a `Serializable`/`Cloneable` collection contract (Wicket state must be
  serializable) — your `ExternalDocumentSearchService` implementation returns one of these.

Every facade method receives an `ExternalDocumentServiceContext`, which exposes `getPlugin()`,
`getPluginConfig()`, `getPluginContext()`, and `getContextModel()` (a `JcrNodeModel` for the document/folder
currently open) plus a generic attribute bag — this is how facade code reaches back into Wicket/Hippo plugin
machinery and the current JCR node.

The concrete facade implementation class name is supplied as a Hippo plugin config parameter
(`PluginConstants.PARAM_EXTERNAL_DOCUMENT_SERVICE_FACADE`); `PluginConstants` (`api/PluginConstants.java`) is the
single source of truth for every other config parameter name/default (selection mode, dialog size, page size,
initial search query, tree theme/expand depth, etc.) — check it before assuming a config key's name or default.

Facades don't have to be instantiated directly: `ServiceDelegatingExternalDocumentServiceFacade`
(`impl/ServiceDelegatingExternalDocumentServiceFacade.java`) implements the same interface but just looks up
*another* facade registered as a Hippo plugin **service** (via `IPluginContext.getService(id, ...)`), keyed by
the `delegate.external.document.service.id` config param — a way to point multiple field/folder plugin configs
at one shared, centrally-registered facade instance instead of duplicating the implementation class per field.

Plugin instantiation is tied to the CMS UI lifecycle: for `fieldpicker`, a facade instance is created when the
matching document (or folder context menu) is opened and destroyed when it's closed — it is not a long-lived
singleton.

Implementation code (beyond the delegating facade) is split into `impl/field/` (document field plugin +
list/tree dialog variants) and `impl/folder/` (folder context-menu plugin + list/tree variants), mirroring the
two install targets described in `src/site/xdoc/field/architecture.xml` and `folder/architecture.xml`.

### richtext-ckeditor: CKEditor 4 plugin

Source is just two files: `plugins/exdocpickerbase/plugin.js` (registers the toolbar button/command) and
`plugins/exdocpickerbase/dialogs/exdocbrowserdialog.js` (the picker dialog UI). There's no Java/Wicket layer here
— it's a client-side CKEditor plugin bundled as CMS resources. A vendored CKEditor build lives only under
`src/test/resources/ckeditor/` for the Jetty manual-test server; it is not part of the production artifact
(production resources are copied to `target/classes/ckeditor/optimized` by the `create-optimized-resources`
Maven execution).

### openui-fieldpicker: Angular app as a Hippo OpenUI extension

The CMS's built-in `OpenUiStringPlugin` loads this Angular app's `index.html` inside an iframe and drives it
entirely through `@bloomreach/ui-extension`'s `UiExtension.register()` API — there is no server-side Java
counterpart to this module beyond the built-in Hippo OpenUI plugin that hosts the iframe.

- `CmsContextService` (`src/app/cms-context.service.ts`) is the single bridge to the host CMS: it registers the
  UI extension, exposes `getUiScope()`, parses the extension's JSON config into a `PickerConfig`
  (`src/app/picker-config.ts`), proxies `getFieldValue()`/`getFieldCompareValue()` to the CMS document field, and
  injects the CMS's own theme CSS (`${uiScope.baseUrl}skin/hippo-cms/css/hippo-cms-theme.min.css`) into the
  iframe document so the picker visually matches the host CMS.
- `PickerConfig` (`findOneUrl`, `findAllUrl`, plus optional `showSearchInput`, `searchOnInit`, `initSearchTerm`,
  `treeViewMode`, `initTreeExpandLevel`) is the entire contract between this generic Angular UI and a backend:
  the app is backend-agnostic and driven purely by these two REST endpoint URLs configured per-field via the
  Hippo namespace's `ui.extension.config`. The expected REST response shape is documented in
  `src/site/xdoc/openui-field/spec-of-rest-services.xml` — implement your backend against that spec, not by
  inspecting the Angular code.
  - `treeViewMode` selects between the flat list dialog (`field-list-dialog/`) and tree dialog
    (`field-tree-dialog/`), analogous to the Flat/Tree List View split in `fieldpicker`.
- `AppComponent.ngOnInit()` sets the Angular `TranslateService` default language from `ui.locale` — i18n follows
  the CMS session's locale, not the browser's.

### demo module

`demo/` (own reactor, parent `hippo-cms7-release`) assembles a runnable Hippo CMS instance wiring in all three
pickers against an example REST data source. Run locally per `demo/README.txt`:

```
mvn clean verify
mvn -P cargo.run                              # boots Essentials/CMS/site in Tomcat via Cargo
mvn -P cargo.run,without-development-data     # skip bootstrapping demo repository content
mvn -P cargo.run -Drepo.path=/path/to/repo     # persist the repo across `mvn clean` runs
```
CMS at `http://localhost:8080/cms`, Essentials setup at `http://localhost:8080/essentials`, site at
`http://localhost:8080/exdocpickerbasedemo`, logs under `target/tomcat9x/logs`. Repository auto-export toggles
from the CMS console (`/cms/console`); the default is controlled in
`repository-data/application/src/main/resources/hcm-config/configuration/modules/autoexport-module.yaml`.
Distribution tarballs: `mvn -P dist` or `mvn -P dist-with-development-data`.
