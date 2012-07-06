package org.eventb.core.internal.ast.extensions.maths;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.extension.StandardGroup;

/**
 * An implementation of an axiomatic type extension.
 * 
 * @author maamria
 * @since 2.0
 *
 */
public class AxiomaticTypeExtension implements IExpressionExtension {

	private final String typeName;
	private final String id;
	private final Object origin;

	public AxiomaticTypeExtension(String typeName, String id, Object origin) {
		this.typeName = typeName;
		this.id = id;
		this.origin = origin;
	}

	@Override
	public Predicate getWDPredicate(IExtendedFormula formula,
			IWDMediator wdMediator) {
		return wdMediator.makeTrueWD();
	}

	@Override
	public String getSyntaxSymbol() {
		return typeName;
	}

	@Override
	public IExtensionKind getKind() {
		// the kind is fixed for the time being
		return ATOMIC_EXPRESSION;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getGroupId() {
		// the group is fixed for the time being
		return StandardGroup.ATOMIC_EXPR.getId();
	}

	@Override
	public void addPriorities(IPriorityMediator mediator) {
		// no priority
	}

	@Override
	public void addCompatibilities(ICompatibilityMediator mediator) {
		// no priority
	}

	@Override
	public Type typeCheck(ExtendedExpression expression,
			ITypeCheckMediator tcMediator) {
		final List<Type> prmTypes = new ArrayList<Type>();
		for (Expression child : expression.getChildExpressions()) {
			final Type alpha = tcMediator.newTypeVariable();
			final PowerSetType prmType = tcMediator.makePowerSetType(alpha);
			tcMediator.sameType(prmType, child.getType());
			prmTypes.add(alpha);
		}
		return tcMediator.makePowerSetType(tcMediator.makeParametricType(
				prmTypes, this));
	}

	@Override
	public Type synthesizeType(Expression[] childExprs,
			Predicate[] childPreds, ITypeMediator mediator) {
		final List<Type> childTypes = new ArrayList<Type>();
		for (Expression child : childExprs) {
			final Type childType = child.getType();
			if (childType == null) {
				return null;
			}
			final Type baseType = childType.getBaseType();
			if (baseType == null) {
				return null;
			}
			childTypes.add(baseType);
		}
		return mediator.makePowerSetType(mediator.makeParametricType(
				childTypes, this));
	}

	@Override
	public boolean verifyType(Type proposedType, Expression[] childExprs,
			Predicate[] childPreds) {
		final Type baseType = proposedType.getBaseType();
		if (baseType == null) {
			return false;
		}
		if (!(baseType instanceof ParametricType)) {
			return false;
		}
		final ParametricType genType = (ParametricType) baseType;
		if (!genType.getExprExtension().equals(this)) {
			return false;
		}
		final Type[] typeParameters = genType.getTypeParameters();
		assert childExprs.length == typeParameters.length;
		for (int i = 0; i < childExprs.length; i++) {
			final Type childType = childExprs[i].getType();
			if (!typeParameters[i].equals(childType.getBaseType())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean conjoinChildrenWD() {
		return true;
	}

	@Override
	public boolean isATypeConstructor() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((typeName == null) ? 0 : typeName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AxiomaticTypeExtension)) {
			return false;
		}
		AxiomaticTypeExtension other = (AxiomaticTypeExtension) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (typeName == null) {
			if (other.typeName != null) {
				return false;
			}
		} else if (!typeName.equals(other.typeName)) {
			return false;
		}
		return true;
	}

	@Override
	public Object getOrigin() {
		return origin;
	}
}
