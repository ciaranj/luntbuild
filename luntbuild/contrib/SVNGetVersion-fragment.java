public static long getSVNVersion(String repositoryURL, String username,
    String password) {
       try {
           SVNRepositoryFactoryImpl.setup();
           DAVRepositoryFactory.setup();
           SVNURL location = SVNURL.parseURIDecoded(repositoryURL);
           SVNRepository repository = SVNRepositoryFactory.create(location);

           ISVNAuthenticationManager authManager =
SVNWCUtil.createDefaultAuthenticationManager(username, password);
           repository.setAuthenticationManager(authManager);

           return repository.getLatestRevision();

       } catch (org.tmatesoft.svn.core.SVNException sve) {
           throw new RuntimeException(sve);
       }
    }