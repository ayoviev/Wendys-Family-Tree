import {Owner} from './owner';
import {Sex} from './sex';

/* In this template we use strings for representating date fields because
 * date input fields provide their value as ISO-formatted string and
 * the backend both expects and delivers the date also in this format.
 * Parsing and formatting of the date is therefore almost never necessary.
 */

export interface Horse {
  id?: number;
  name: string;
  description?: string;
  dateOfBirth: string; // See above comment for why this is a string
  sex: Sex;
  image?: string;
  owner?: Owner;
  mother?: Horse;
  father?: Horse;
}

export interface HorseSearch {
  name?: string;
  description?: string;
  bornBefore?: string;
  sex?: Sex;
  ownerName?: string;
  limit?: number;
}

export interface HorseCreate {
  name: string;
  description?: string;
  dateOfBirth: string;
  sex: Sex;
  image?: string;
  ownerId?: number;
  motherId?: number;
  fatherId?: number;
}

export function convertFromHorseToCreate(horse: Horse): HorseCreate {
  return {
    name: horse.name,
    description: horse.description,
    dateOfBirth: horse.dateOfBirth,
    sex: horse.sex,
    image: horse.image,
    ownerId: horse.owner?.id,
    motherId: horse.mother?.id,
    fatherId: horse.father?.id,
  };
}

export interface HorseTreeNode {
  id: number;
  name: string;
  dateOfBirth: string;
  mother?: HorseTreeNode;
  father?: HorseTreeNode;
}

