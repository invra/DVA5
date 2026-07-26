{
  description = "A very basic flake";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
  };

  outputs = { nixpkgs, ... }@inputs:
  let
    forAllSystems = nixpkgs.lib.genAttrs nixpkgs.lib.systems.flakeExposed;
  in
  {
    devShells = forAllSystems (system: let
      pkgs = import nixpkgs { inherit system; };
    in rec {
      dva5 = pkgs.callPackage ./nix/devshell.nix { inherit inputs; };
      default = dva5;
    });
    # packages = forAllSystems (system: let
    #   pkgs = import nixpkgs { inherit system; };
    # in rec {
    #   dva5 = pkgs.callPackage ./nix/build.nix { inherit inputs; } { };
    #   default = dva5;
    # });
  };    
}

