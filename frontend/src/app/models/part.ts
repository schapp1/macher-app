export interface Part {
  id: string;
  idlNumber: string;
  partNumber: string;
  level: string;
  children: Part[];
  matShortText: string;
  isAssey: boolean;
}

export interface PartCreationRequest { partNumber: string }
