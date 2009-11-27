package ac.soton.eventb.ruleBase.theory.core.sc.states;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;

@SuppressWarnings("restriction")
public class GivenSets extends State implements IGivenSets{

	private List<String> givenSets;
	
	public GivenSets(){
		givenSets = new ArrayList<String>();
	}
	
	@Override
	public void addGivenSet(String set) {
		
		givenSets.add(set);
		
	}

	@Override
	public List<String> getGivenSets() {
		return givenSets;
	}

	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}
}
