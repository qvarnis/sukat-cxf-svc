package se.su.it.svc

import se.su.it.svc.commons.SvcAudit
import javax.jws.WebService
import javax.jws.WebParam
import se.su.it.svc.ldap.SuPerson
import se.su.it.svc.query.SuPersonQuery
import se.su.it.svc.manager.GldapoManager
import se.su.it.svc.query.SuCardQuery
import org.apache.log4j.Logger

/**
 * Implementing class for EntitlementService CXF Web Service.
 * This Class handles all Entitlement admin activities in SUKAT.
 */
@WebService
public class EntitlementServiceImpl implements EntitlementService {
  private static final Logger logger = Logger.getLogger(EntitlementServiceImpl.class)

  public void addEntitlement(@WebParam(name = "uid") String uid, @WebParam(name = "entitlement") String entitlement, @WebParam(name = "audit") SvcAudit audit) {
    if(uid == null || entitlement == null || audit == null)
      throw new java.lang.IllegalArgumentException("Null values not allowed in this function")

    SuPerson person = SuPersonQuery.getSuPersonFromUID(GldapoManager.LDAP_RW, uid)
    if(person) {
      if(person.eduPersonEntitlement != null) {
        if(person.eduPersonEntitlement.find {it.equalsIgnoreCase(entitlement)})
          throw new java.lang.IllegalArgumentException("Entitlement ${entitlement} already exist")
        person.eduPersonEntitlement.add(entitlement)
        SuPersonQuery.saveSuPerson(person)
      } else {
        def tmpSet = new java.util.LinkedHashSet<String>()
        tmpSet.add(entitlement)
        person.eduPersonEntitlement = tmpSet
        SuPersonQuery.saveSuPerson(person)
      }
    } else {
      logger.info("addEntitlement: Could not find uid<${uid}>")
      throw new IllegalArgumentException("addEntitlement no such uid found: "+uid)
    }
  }
}
