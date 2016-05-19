package com.vesperin.cue.bsg;

import com.vesperin.base.Context;
import com.vesperin.base.EclipseJavaParser;
import com.vesperin.base.JavaParser;
import com.vesperin.base.locations.Locations;
import com.vesperin.base.locators.UnitLocation;
import com.vesperin.cue.bsg.visitors.BlockSegmentationVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Huascar Sanchez
 */
public class BSGTest {
  private static final JavaParser PARSER = new EclipseJavaParser();
  private static final String TRY_CATCH   = "Try/Catch";
  private static final String WHILE_LOOP  = "While/Loop";
  private static final Map<String, Context> CONTEXT;

  static {
    CONTEXT = new HashMap<>();
    CONTEXT.put(TRY_CATCH, PARSER.parseJava(Code.TRY_CATCH));
    CONTEXT.put(WHILE_LOOP, PARSER.parseJava(Code.WHILE_LOOP));
  }


  @Test public void testBSGFromMethod() throws Exception {

    final Context context = CONTEXT.get(TRY_CATCH);

    final List<UnitLocation> locatedUnitList = context.locateMethods().stream()
      .filter(method("main"))
      .collect(Collectors.toList());

    final UnitLocation locatedUnit = locatedUnitList.get(0);
    assertNotNull(locatedUnit);

    final BlockSegmentationVisitor blockSegmentation = new BlockSegmentationVisitor(locatedUnit);
    locatedUnit.getUnitNode().accept(blockSegmentation);

    // the method declaration should have zero..or least two
    final BlockSegmentationGraph graph = blockSegmentation.getBlockSegmentationGraph();
    assertThat(graph.getVertices().size() == 5, is(true));
    assertThat(graph.getEdges().size() == 6, is(true));

  }

  @Test public void testBSGFromClass() throws Exception {
    final Context context = CONTEXT.get(TRY_CATCH);
    final List<UnitLocation> locatedUnitList = context.locateUnit(Locations.locate(context.getCompilationUnit()));
    final UnitLocation locatedUnit = locatedUnitList.get(0);
    assertNotNull(locatedUnit);

    final BlockSegmentationVisitor blockSegmentation = new BlockSegmentationVisitor(locatedUnit);
    locatedUnit.getUnitNode().accept(blockSegmentation);

    final BlockSegmentationGraph graph = blockSegmentation.getBlockSegmentationGraph();
    assertThat(graph.getVertices().size() == 6, is(true));
    assertThat(graph.getEdges().size() == 7, is(true));
  }

  @Test public void testBSGWhileLoop() throws Exception {
    final Context context = CONTEXT.get(WHILE_LOOP);

    final List<UnitLocation> locatedUnitList = context.locateMethods().stream()
      .filter(method("count2Div"))
      .collect(Collectors.toList());

    final UnitLocation locatedUnit = locatedUnitList.get(0);
    assertNotNull(locatedUnit);

    final BlockSegmentationVisitor blockSegmentation = new BlockSegmentationVisitor(locatedUnit);
    locatedUnit.getUnitNode().accept(blockSegmentation);

    final BlockSegmentationVisitor segmentationVisitor = new BlockSegmentationVisitor(locatedUnit);
    locatedUnit.getUnitNode().accept(segmentationVisitor);


    assertNotNull(segmentationVisitor.getBlockSegmentationGraph());

    final BlockSegmentationGraph graph = blockSegmentation.getBlockSegmentationGraph();
    assertNotNull(graph);

  }

  @Test public void testNewSegmentationVisitor() throws Exception {
    final Context context = CONTEXT.get(TRY_CATCH);
    final List<UnitLocation> locatedUnitList = context.locateMethods().stream()
      .filter(method("main"))
      .collect(Collectors.toList());
    final UnitLocation locatedUnit = locatedUnitList.get(0);
    assertNotNull(locatedUnit);

    final BlockSegmentationVisitor segmentationVisitor = new BlockSegmentationVisitor(locatedUnit);
    locatedUnit.getUnitNode().accept(segmentationVisitor);

    final BlockSegmentationGraph graph = segmentationVisitor.getBlockSegmentationGraph();

    assertNotNull(graph);
    assertThat(graph.getVertices().size() == 5, is(true));
    assertThat(graph.getEdges().size() == 6, is(true));
  }

  private static Predicate<UnitLocation> method(final String name){
    return (u -> ((MethodDeclaration)u.getUnitNode())
      .getName().getIdentifier().equals(name));
  }
}
