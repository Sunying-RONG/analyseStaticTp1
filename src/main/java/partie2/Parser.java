package partie2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.Statement;

public class Parser {
	
	public static final String projectPath = "/Users/rongsunying/eclipse-workspace/test1carre/";
	public static final String projectSourcePath = projectPath + "/src";
	// MacOs
	public static final String jrePath = "/Library/Java/JavaVirtualMachines/jdk1.8.0_221.jdk/Contents/Home/jre/";
	// Linux
//	public static final String jrePath = "/usr/lib/jvm/java-8-openjdk-amd64/jre/";
	
	public static float classesNumber;
	public static float methodsNumber;
	public static List<String> packages = new ArrayList<>();
	public static float fieldNumber;
	public static float statementsNumber;
	public static float statementsAllNumber;
	public static Map<String, Float> class_methodsNumber = new HashMap<String, Float>();
	public static Map<String, Float> class_fieldsNumber = new HashMap<String, Float>();
	
	public static void main(String[] args) throws IOException {

		// read java files
		final File folder = new File(projectSourcePath);
		ArrayList<File> javaFiles = listJavaFilesForFolder(folder);

		// every file
		for (File fileEntry : javaFiles) {
			String content = FileUtils.readFileToString(fileEntry);
			// System.out.println(content);

			CompilationUnit parse = parse(content.toCharArray());
			// print package info
			printPackageInfo(parse);
			
			// print class info
			printClassInfo(parse);
			
			// print field info
			printFieldInfo(parse);
			
			// print methods info
			printMethodInfo(parse);

			// print variables info
			printVariableInfo(parse);
			
			// print method invocations
			printMethodInvocationInfo(parse);
			
			// print statement info
			printStatementInfo(parse);
			
			// print all statement info
			printAllStatementInfo(parse);
			System.out.println("------------");

		}
		// whole application
		System.out.println("Class number of application: " + classesNumber);
		System.out.println("Method number of application: " + methodsNumber);
		System.out.println("Package number of application: " + packages.size());
		System.out.println("Average method number per class: " + methodsNumber/classesNumber);
		System.out.println("Field declaration number of application: " + fieldNumber);
		System.out.println("Average field declaration number per class: " + fieldNumber/classesNumber);
		System.out.println("All statement number of application: " + statementsAllNumber);
		System.out.println("Statement number of application's methods: " + statementsNumber);
		System.out.println("Average statement number per method: " + statementsNumber/methodsNumber);
	}

	// read all java files from specific folder
	public static ArrayList<File> listJavaFilesForFolder(final File folder) {
		ArrayList<File> javaFiles = new ArrayList<File>();
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				javaFiles.addAll(listJavaFilesForFolder(fileEntry));
			} else if (fileEntry.getName().contains(".java")) {
				// System.out.println(fileEntry.getName());
				javaFiles.add(fileEntry);
			}
		}

		return javaFiles;
	}

	// create AST
	private static CompilationUnit parse(char[] classSource) {
		ASTParser parser = ASTParser.newParser(AST.JLS4); // java +1.6
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
 
		parser.setBindingsRecovery(true);
 
		Map options = JavaCore.getOptions();
		parser.setCompilerOptions(options);
 
		parser.setUnitName("");
 
		String[] sources = { projectSourcePath }; 
		String[] classpath = {jrePath};
 
		parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
		parser.setSource(classSource);
		
		return (CompilationUnit) parser.createAST(null); // create and parse
	}
	
	// navigate package information
	public static void printPackageInfo(CompilationUnit parse) {
		PackageDeclarationVisitor visitor = new PackageDeclarationVisitor();
		parse.accept(visitor);
		
		for (PackageDeclaration pack : visitor.getPackages()) {
			System.out.println("Package: " + pack);
			if (!packages.contains(pack.toString())) {
				packages.add(pack.toString());
			}
		}
	}

	// navigate class information
	public static void printClassInfo(CompilationUnit parse) {
		ClassDeclarationVisitor visitor = new ClassDeclarationVisitor();
		parse.accept(visitor);

		for (TypeDeclaration clas : visitor.getClasses()) {
			System.out.println("Class name: " + clas.getName());
		}
		classesNumber = classesNumber + visitor.getClassesNumber();
	}
	
	// navigate field information
	public static void printFieldAccessInfo(CompilationUnit parse) {
		FieldAccessVisitor visitor = new FieldAccessVisitor();
		parse.accept(visitor);
		
		for (SimpleName field : visitor.getFields()) {
			System.out.println("Field access name: " + field.getFullyQualifiedName());
		}
	}
	
	// navigate field information
	public static void printFieldInfo(CompilationUnit parse) {
		FieldDeclarationVisitor visitor = new FieldDeclarationVisitor();
		parse.accept(visitor);
		
		for (FieldDeclaration field : visitor.getFields()) {
			System.out.println("Field name: " + field);
		}
		fieldNumber = fieldNumber + visitor.getFieldsNumber();
	}
		
	// navigate method information
	public static void printMethodInfo(CompilationUnit parse) {
		MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
		parse.accept(visitor);

		for (MethodDeclaration method : visitor.getMethods()) {
			System.out.println("Method name: " + method.getName()
					+ " Return type: " + method.getReturnType2());
		}
		methodsNumber = methodsNumber + visitor.getMethodsNumber();

	}

	// navigate variables inside method
	public static void printVariableInfo(CompilationUnit parse) {

		MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
		parse.accept(visitor1);
		for (MethodDeclaration method : visitor1.getMethods()) {

			VariableDeclarationFragmentVisitor visitor2 = new VariableDeclarationFragmentVisitor();
			method.accept(visitor2);

			for (VariableDeclarationFragment variableDeclarationFragment : visitor2
					.getVariables()) {
				System.out.println("variable name: "
						+ variableDeclarationFragment.getName()
						+ ". variable Initializer: "
						+ variableDeclarationFragment.getInitializer());
			}

		}
	}
	
	// navigate statements inside method
	public static void printStatementInfo(CompilationUnit parse) {
		
		MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
		parse.accept(visitor1);
		for (MethodDeclaration method : visitor1.getMethods()) {
			
			StatementVisitor visitor2 = new StatementVisitor();
			method.accept(visitor2);
			
			for (Statement state : visitor2.getStatements()) {
//				System.out.println("Statement: "+state);
			}
			System.out.println("Statement number of this method: " + visitor2.getStatementsNumber());
			statementsNumber = statementsNumber + visitor2.getStatementsNumber();
		}
	}
	
	// navigate all statements in application
	public static void printAllStatementInfo(CompilationUnit parse) {
		
		StatementAllVisitor visitor = new StatementAllVisitor();
		parse.accept(visitor);
		
//			for (Statement state : visitor.getStatements()) {
//				System.out.println("Statement: "+state);
//			}
		statementsAllNumber = statementsAllNumber + visitor.getStatementsNumber();
	}
	
	// navigate method invocations inside method
	public static void printMethodInvocationInfo(CompilationUnit parse) {

		MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
		parse.accept(visitor1);
		for (MethodDeclaration method : visitor1.getMethods()) {

			MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
			method.accept(visitor2);

			for (MethodInvocation methodInvocation : visitor2.getMethods()) {
				System.out.println("method " + method.getName() + " invoc method "
						+ methodInvocation.getName());
			}

		}
	}
	

}
