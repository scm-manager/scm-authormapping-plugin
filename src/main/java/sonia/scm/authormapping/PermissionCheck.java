package sonia.scm.authormapping;

import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

final class PermissionCheck {

  private static final String ACTION = "authormapping";

  private PermissionCheck() {
  }

  static boolean isPermitted(Repository repository) {
    return RepositoryPermissions.custom(ACTION, repository).isPermitted();
  }

  static void check(Repository repository) {
    RepositoryPermissions.custom(ACTION, repository).check();
  }
}
