/*
 * Copyright 2019 the original author or authors.
 * Copyright 2019 SorcerSoft.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sorcer.core.service;

import net.jini.core.transaction.Transaction;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sorcer.core.context.ModelStrategy;
import sorcer.core.context.ServiceContext;
import sorcer.core.context.model.ent.EntryAnalyzer;
import sorcer.core.context.model.ent.EntryExplorer;
import sorcer.core.plexus.FidelityManager;
import sorcer.service.*;
import sorcer.service.Discipline;
import sorcer.service.modeling.Functionality;
import sorcer.service.modeling.cxtn;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Collaboration implements Contextion, Transdomain, Dependency, cxtn {

	private static final long serialVersionUID = 1L;

	protected static Logger logger = LoggerFactory.getLogger(Collaboration.class.getName());

	private static int count = 0;

	protected Uuid id = UuidFactory.generate();

	protected  String name;

    // the input of this collaboration
    protected Context input;

	protected ServiceFidelity contextMultiFi;

    // the output of this collaboration
    protected Context output;

    protected Fi multiFi;

	protected Morpher morpher;

	protected Fidelity<Analysis> analyzerFi;

	protected Fidelity<EntryExplorer> explorerFi;

	protected Map<String, Domain> domains = new HashMap<>();

	// active disciplnes
	protected Paths domainPaths;

	// dependency management for this collaboration
	protected List<Evaluation> dependers = new ArrayList<Evaluation>();


	private FidelityManager fiManager;

	protected MogramStrategy mogramStrategy;

	// context input connector
	protected Context inConnector;

	// context output connector
	protected Context outConnector;

	protected Context scope;

    public Collaboration() {
        this(null);
    }

    public Collaboration(String name) {
        if (name == null) {
            this.name = getClass().getSimpleName() + "-" + count++;
        } else {
            this.name = name;
        }
		mogramStrategy = new ModelStrategy(this);
    }

    public Collaboration(String name, Domain[] domains) {
        this(name);
        for (Domain domain : domains) {
                this.domains.put(domain.getName(), domain);
        }
    }

	public Collaboration(String name, List<Domain> domains) {
		this(name);
		for (Domain domain : domains) {
			this.domains.put(domain.getName(), domain);
		}
	}

    public Context getOutput(Arg... args) {
        return output;
    }

    public void setOutput(Context output) {
        this.output = output;
    }

    public Paths getDomainPaths() {
        return domainPaths;
    }

    public void setDomainPaths(Paths domainPaths) {
        this.domainPaths = domainPaths;
    }

    public Map<String, Domain> getDomains() {
		return domains;
	}

	public Domain getDomain(String name) {
		return domains.get(name);
	}

	public Fidelity<EntryExplorer> getExplorerFi() {
		return explorerFi;
	}

	public void setExplorerFi(Fidelity<EntryExplorer> explorerFi) {
		this.explorerFi = explorerFi;
	}

	// default instance new Return(Context.RETURN);
	protected Context.Return contextReturn;

	@Override
	public Context getContext() throws ContextException {
		return input;
	}

	@Override
	public void setContext(Context input) throws ContextException {
		this.input = input;
	}

	@Override
	public Context appendContext(Context context) throws ContextException, RemoteException {
		return input.appendContext(context);
	}

	@Override
	public Context getContext(Context contextTemplate) throws RemoteException, ContextException {
		return null;
	}

	@Override
	public Context appendContext(Context context, String path) throws ContextException, RemoteException {
		return null;
	}

	@Override
	public Context getContext(String path) throws ContextException, RemoteException {
		return null;
	}

	@Override
	public Context.Return getContextReturn() {
		return contextReturn;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Fi getMultiFi() {
		return multiFi;
	}

	@Override
	public Morpher getMorpher() {
		return morpher;
	}

	@Override
	public Object getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	public Fidelity<Analysis> getAnalyzerFi() {
		return analyzerFi;
	}

	public void setAnalyzerFi(Fidelity<Analysis> analyzerFi) {
		this.analyzerFi = analyzerFi;
	}

	public List<Domain> getDisciplineList() {
		List<Domain> domainList = new ArrayList<>();
		for (Domain disc : domains.values()) {
			if (disc instanceof Discipline) {
				domainList.add(disc);
			}
		}
		return domainList;
	}

	public Fidelity<EntryExplorer> setExplorerFi(Context context) throws ConfigurationException {
		if(explorerFi == null) {
			Object exploreComponent = context.get(Context.EXPLORER_PATH);
			if (exploreComponent != null) {
				if (exploreComponent instanceof EntryExplorer) {
					explorerFi = new Fidelity(((EntryExplorer)exploreComponent).getName());
					explorerFi.addSelect((EntryExplorer) exploreComponent);
					explorerFi.setSelect((EntryExplorer)exploreComponent);
					((EntryAnalyzer)exploreComponent).setContextion(this);
				} else if (exploreComponent instanceof ServiceFidelity
					&& ((ServiceFidelity) exploreComponent).getFiType().equals(Fi.Type.EXPLORER)) {
					explorerFi = (Fidelity) exploreComponent;
					explorerFi.getSelect().setContextion(this);
				}

			}
		}
		((ServiceContext)context).getMogramStrategy().setExecState(Exec.State.INITIAL);
		if (output == null) {
			output = new ServiceContext(name);
		}
		return explorerFi;
	}

	public Fidelity<Analysis> setAnalyzerFi(Context context) throws ConfigurationException {
		if(analyzerFi == null) {
			Object mdaComponent = context.get(Context.MDA_PATH);
			if (mdaComponent != null) {
				if (mdaComponent instanceof EntryAnalyzer) {
					analyzerFi = new Fidelity(((EntryAnalyzer)mdaComponent).getName());
					analyzerFi.addSelect((EntryAnalyzer) mdaComponent);
					analyzerFi.setSelect((EntryAnalyzer)mdaComponent);
					((EntryAnalyzer)mdaComponent).setContextion(this);
				} else if (mdaComponent instanceof ServiceFidelity
						&& ((ServiceFidelity) mdaComponent).getFiType().equals(Fi.Type.MDA)) {
					analyzerFi = (Fidelity) mdaComponent;
					((EntryAnalyzer)analyzerFi.getSelect()).setContextion(this);
				}
			}
		}
		((ServiceContext)context).getMogramStrategy().setExecState(Exec.State.INITIAL);
		if (output == null) {
			output = new ServiceContext(name);
		}
		return analyzerFi;
	}

	public Context getInConnector() {
		return inConnector;
	}

	public void setInConnector(Context inConnector) {
		this.inConnector = inConnector;
	}

	public Context getOutConnector() {
		return outConnector;
	}

	public void setOutConnector(Context outConnector) {
		this.outConnector = outConnector;
	}

	public FidelityManager getFiManager() {
		return fiManager;
	}

	public void setFiManager(FidelityManager fiManager) {
		this.fiManager = fiManager;
	}

	public MogramStrategy getMogramStrategy() {
		return mogramStrategy;
	}

	public void setModelStrategy(MogramStrategy strategy) {
		mogramStrategy = strategy;
	}

	@Override
	public Object execute(Arg... args) throws ServiceException, RemoteException {
		Context context = Arg.selectContext(args);
		Context out = null;
		out = evaluate(context, args);
		return out;
	}

	@Override
	public Context evaluate(Context context, Arg... args) throws EvaluationException, RemoteException {
		Context out = null;
		Context cxt = context;
		try {
			if (cxt == null) {
				cxt = getInput();
			}
			ModelStrategy strategy = ((ModelStrategy) cxt.getMogramStrategy());
			List<Fidelity> fis = Arg.selectFidelities(args);
			if (analyzerFi != null) {
				strategy.setExecState(Exec.State.RUNNING);
				// select mda Fi if provided
				for (Fi fi : fis) {
					if (analyzerFi.getName().equalsIgnoreCase(fi.getPath())) {
						analyzerFi.selectSelect(fi.getName());
					}
				}
				((EntryAnalyzer)analyzerFi.getSelect()).setContextion(this);
				logger.info("*** analyzerFi: {}", ((EntryAnalyzer)analyzerFi.getSelect()).getName());
			}
			if (explorerFi != null) {
				for (Fi fi : fis) {
					if (analyzerFi.getName().equalsIgnoreCase(fi.getPath())) {
						analyzerFi.selectSelect(fi.getName());
					}
				}
				explorerFi.getSelect().setContextion(this);
				logger.info("*** explorerFi: {}", explorerFi.getSelect().getName());
			}

			if (analyzerFi == null) {
				setAnalyzerFi(cxt);
			}
			if (explorerFi == null) {
				setExplorerFi(cxt);
			}

			out = explorerFi.getSelect().explore(cxt);
			((ModelStrategy)mogramStrategy).setOutcome(output);
			strategy.setExecState(Exec.State.DONE);
		} catch (ConfigurationException | ContextException e) {
			throw new EvaluationException(e);
		}
		return out;
	}

	public ServiceFidelity getContextMultiFi() {
		return contextMultiFi;
	}

	public void setContextMultiFi(ServiceFidelity contextMultiFi) {
		this.contextMultiFi = contextMultiFi;
	}

	public Context getInput() throws ContextException {
		// if no contextMultiFi then return direct input
		if (contextMultiFi == null || contextMultiFi.getSelect() == null) {
			return input;
		}
		input = (Context) contextMultiFi.getSelect();
		return input;
	}

	public Context setInput(Context input) throws ContextException {
		if (contextMultiFi == null) {
			contextMultiFi = new ServiceFidelity();
		}
		contextMultiFi.getSelects().add(input);
		contextMultiFi.setSelect(input);

		this.input = input;
		return input;
	}

	@Override
	public Context exert(Transaction txn, Arg... args) throws ContextException, RemoteException {
		return evaluate(input, args);
	}

	public void reportException(String message, Throwable t) {
		mogramStrategy.addException(t);
	}

	public void reportException(String message, Throwable t, ProviderInfo info) {
		// reimplement in sublasses
		mogramStrategy.addException(t);
	}

	public void reportException(String message, Throwable t, Exerter provider) {
		// reimplement in sublasses
		mogramStrategy.addException(t);
	}

	public void reportException(String message, Throwable t, Exerter provider, ProviderInfo info) {
		// reimplement in sublasses
		mogramStrategy.addException(t);
	}

	public Collaboration addDepender(Evaluation depender) {
		if (this.dependers == null)
			this.dependers = new ArrayList<Evaluation>();
		dependers.add(depender);
		return this;
	}

	@Override
	public void addDependers(Evaluation... dependers) {
		if (this.dependers == null)
			this.dependers = new ArrayList<Evaluation>();
		for (Evaluation depender : dependers)
			this.dependers.add(depender);
	}

	@Override
	public List<Evaluation> getDependers() {
		return dependers;
	}

	@Override
	public Map<String, Domain> getChildren() {
		return domains;
	}

	@Override
	public Mogram getChild(String name) {
		return domains.get(name);
	}

	@Override
	public Context getScope() {
		return scope;
	}

	@Override
	public void setScope(Context scope) {
		this.scope = scope;;
	}

	@Override
	public Object get(String path$domain) {
		String path = null;
		String domain = null;
		if (path$domain.indexOf("$") > 0) {
			int ind = path$domain.indexOf("$");
			path = path$domain.substring(0, ind);
			domain = path$domain.substring(ind + 1);
			return getChild(domain).get(path);
		} else if (path$domain != null) {
			return getChild(path$domain);
		}
		return null;
	}

	@Override
	public List<Contextion> getContextions(List<Contextion> contextionList) {
		for (Contextion e : getChildren().values()) {
			e.getContextions(contextionList);
		}
		contextionList.add(this);
		return contextionList;
	}

	public Functionality.Type getDependencyType() {
		return Functionality.Type.COLLABORATION;
	}
	@Override
	public void selectFidelity(Fidelity fi) throws ConfigurationException {

	}
}
