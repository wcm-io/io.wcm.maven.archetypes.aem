#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.components;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

#if ($optionWcmioHandler == 'y')
import io.wcm.sling.commons.adapter.AdaptTo;
#end
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import ${package}.testcontext.AppAemContext;

@ExtendWith(AemContextExtension.class)
class CurrentDateTest {

  private final AemContext context = AppAemContext.newAemContext();

  private CurrentDate underTest;

  @BeforeEach
  void setUp() {
#if ($optionWcmioHandler == 'y')
    underTest = AdaptTo.notNull(context.request(), CurrentDate.class);
#else
    underTest = context.request().adaptTo(CurrentDate.class);
#end
  }

  @Test
  void testYear() {
    assertTrue(underTest.getYear() >= 2018);
  }

}
