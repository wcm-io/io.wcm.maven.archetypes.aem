#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.models;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import io.wcm.testing.mock.aem.junit.AemContext;

import ${package}.testcontext.AppAemContext;

public class CurrentDateTest {

  @Rule
  public AemContext context = AppAemContext.newAemContext();

  private CurrentDate underTest;

  @Before
  public void setUp() {
    underTest = context.request().adaptTo(CurrentDate.class);
  }

  @Test
  public void testYear() {
    assertTrue(underTest.getYear() >= 2016);
  }

}
