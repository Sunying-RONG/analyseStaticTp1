package partie2;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class StatementAllVisitor extends ASTVisitor {
	int statementsNumber;
	int m_nStatementCount;
	
	public boolean visit(PackageDeclaration node) {
		m_nStatementCount = 1;
		return true;
	}
	
	public int getStatementsNumber() {
		return m_nStatementCount;
	}
//	public void endVisit(Statement node) {
//		System.out.println("Statement count for method: " + m_nStatementCount);
//	}
	
	public boolean visit (TypeDeclaration node) {
		m_nStatementCount++;
		return true;
	}
	
	public boolean visit (FieldDeclaration node) {
		m_nStatementCount++;
		return true;
	}
	
	public boolean visit (Block node) {
		m_nStatementCount++;
		return true;
	}
	public boolean visit (BreakStatement node) {
		m_nStatementCount++;
		return true;
	}
	public boolean visit (ConstructorInvocation node) {
		m_nStatementCount++;
		return true;
	}
	public boolean visit (ContinueStatement node) {
		m_nStatementCount++;
		return true;
	}
	public boolean visit (DoStatement node) {
		m_nStatementCount++;
		return true;
	}
	
	public boolean visit (EmptyStatement node) {
		m_nStatementCount++;
		return true;
	}
	
	public boolean visit (EnhancedForStatement node) {
		m_nStatementCount++;
		return true;
	}
	
	public boolean visit (ExpressionStatement node) {
		m_nStatementCount++;
		return true;
	}
	
	public boolean visit (ForStatement node) {
		m_nStatementCount++;
		return true;
	}
	
	public boolean visit (IfStatement node) {
		m_nStatementCount++;
		return true;
	}
	
	public boolean visit (LabeledStatement node) {
		m_nStatementCount++;
		return true;
	}
	
	public boolean visit (ReturnStatement node) {
		m_nStatementCount++;
		return true;
	}
	
	public boolean visit (SwitchCase node) {
		m_nStatementCount++;
		return true;
	}
	
	public boolean visit (SwitchStatement node) {
		m_nStatementCount++;
		return true;
	}
	
	public boolean visit (SynchronizedStatement node) {
		m_nStatementCount++;
		return true;
	}
	
	public boolean visit (ThrowStatement node) {
		m_nStatementCount++;
		return true;
	}
	
	public boolean visit (TryStatement node) {
		m_nStatementCount++;
		return true;
	}
	
	public boolean visit (TypeDeclarationStatement node) {
		m_nStatementCount++;
		return true;
	}
	
	public boolean visit (VariableDeclarationStatement node) {
		m_nStatementCount++;
		return true;
	}
	
	public boolean visit (WhileStatement node) {
		m_nStatementCount++;
		return true;
	}
	
	
	
}
